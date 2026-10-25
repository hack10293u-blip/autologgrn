package vn.codex.garenapopup;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class MainActivity extends Activity {
    private static final int PICK_FILE = 7, SAVE_FILE = 8;
    private WebView webView;
    private ValueCallback<Uri[]> fileChooserCallback;
    private String pendingManualContent = "", pendingManualName = "garena-accounts.txt";

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(Color.rgb(242,239,248));
        getWindow().setNavigationBarColor(Color.rgb(242,239,248));
        if (Build.VERSION.SDK_INT >= 26) getWindow().getDecorView().setSystemUiVisibility(android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        webView = new WebView(this);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(false);
        webView.setBackgroundColor(Color.rgb(242,239,248));
        webView.addJavascriptInterface(new AndroidBridge(), "AndroidBridge");
        webView.setWebChromeClient(new WebChromeClient() {
            @Override public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> callback, FileChooserParams params) {
                if (fileChooserCallback != null) fileChooserCallback.onReceiveValue(null);
                fileChooserCallback = callback;
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("text/plain");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                try { startActivityForResult(intent, PICK_FILE); }
                catch (Exception e) { fileChooserCallback = null; callback.onReceiveValue(null); }
                return true;
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                if ("https".equalsIgnoreCase(uri.getScheme()) && uri.getHost() != null && uri.getHost().equalsIgnoreCase("t.me")) {
                    try { startActivity(new Intent(Intent.ACTION_VIEW, uri)); } catch (Exception ignored) {}
                    return true;
                }
                return false;
            }
            @Override public void onPageFinished(WebView view, String url) { syncPermissionStates(); }
        });
        setContentView(webView);
        webView.loadUrl("file:///android_asset/app-runtime.html");
    }

    private class AndroidBridge {
        @JavascriptInterface public String getAccountsJson() {
            return getSharedPreferences("login", MODE_PRIVATE).getString("accounts_json", "[]");
        }
        @JavascriptInterface public int getSelectedIndex() { return getSharedPreferences("login", MODE_PRIVATE).getInt("selectedIndex", 0); }
        @JavascriptInterface public boolean isPopupEnabled() { return getSharedPreferences("login", MODE_PRIVATE).getBoolean("popup_enabled", false); }
        @JavascriptInterface public void saveAccounts(String json) {
            try {
                JSONArray list = new JSONArray(json);
                getSharedPreferences("login", MODE_PRIVATE).edit().putString("accounts_json", list.toString()).putInt("selectedIndex", 0).putBoolean("fillPending", false).putString("state", list.length() == 0 ? "Chưa có danh sách tài khoản" : "Danh sách đã nạp").apply();
                if (list.length() > 0) {
                    JSONObject a = list.getJSONObject(0);
                    getSharedPreferences("login", MODE_PRIVATE).edit().putString("user", a.optString("user", "")).putString("pass", a.optString("pass", "")).apply();
                }
            } catch (Exception ignored) {}
        }
        @JavascriptInterface public void selectAccount(int index) {
            try {
                JSONArray list = new JSONArray(getAccountsJson());
                if (list.length() == 0) return;
                final int selected = (index % list.length() + list.length()) % list.length();
                JSONObject a = list.getJSONObject(selected);
                getSharedPreferences("login", MODE_PRIVATE).edit().putInt("selectedIndex", selected).putString("user", a.optString("user", "")).putString("pass", a.optString("pass", "")).putBoolean("fillPending", true).putInt("loginStage", 0).putString("state", "Sẵn sàng").apply();
                sendBroadcast(new Intent("vn.codex.garenapopup.COMMAND").putExtra("command", "FILL_ACCOUNT"));
            } catch (Exception ignored) {}
        }
        @JavascriptInterface public boolean setPopupEnabled(boolean enabled) {
            if (!enabled) { getSharedPreferences("login", MODE_PRIVATE).edit().putBoolean("popup_enabled", false).apply(); stopService(new Intent(MainActivity.this, FloatingService.class)); return true; }
            if (!Settings.canDrawOverlays(MainActivity.this)) { runOnUiThread(() -> startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())))); return false; }
            getSharedPreferences("login", MODE_PRIVATE).edit().putBoolean("popup_enabled", true).apply(); startService(new Intent(MainActivity.this, FloatingService.class));
            if (getSharedPreferences("login", MODE_PRIVATE).getBoolean("autoOpen", false)) runOnUiThread(MainActivity.this::openGame);
            return true;
        }
        @JavascriptInterface public void setTheme(String theme) {
            boolean dark = "edras".equals(theme) || "neon".equals(theme);
            runOnUiThread(() -> {
                int bar = dark ? Color.rgb(16,14,24) : Color.rgb(242,239,248);
                getWindow().setStatusBarColor(bar);
                getWindow().setNavigationBarColor(dark ? bar : Color.WHITE);
                int flags = dark ? 0 : (android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
                getWindow().getDecorView().setSystemUiVisibility(flags);
            });
        }
        @JavascriptInterface public void setPreference(String key, String value) {
            if (key == null || value == null) return;
            android.content.SharedPreferences.Editor edit = getSharedPreferences("login", MODE_PRIVATE).edit();
            if ("autoOpen".equals(key) || "autoSubmit".equals(key)) edit.putBoolean(key, Boolean.parseBoolean(value));
            else if ("overlayOpacity".equals(key) || "overlaySize".equals(key) || "operationDelay".equals(key)) edit.putString(key, value);
            edit.apply();
            if (("overlayOpacity".equals(key) || "overlaySize".equals(key)) && isPopupEnabled()) {
                stopService(new Intent(MainActivity.this, FloatingService.class));
                startService(new Intent(MainActivity.this, FloatingService.class));
            }
        }
        @JavascriptInterface public String getPreference(String key, String fallback) {
            android.content.SharedPreferences pref = getSharedPreferences("login", MODE_PRIVATE);
            if ("autoOpen".equals(key) || "autoSubmit".equals(key)) return String.valueOf(pref.getBoolean(key, Boolean.parseBoolean(fallback)));
            return pref.getString(key, fallback);
        }
        @JavascriptInterface public void clearAppCache() { runOnUiThread(() -> { webView.clearCache(true); webView.clearHistory(); }); }
        @JavascriptInterface public void copyText(String text) {
            ClipboardManager manager = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
            manager.setPrimaryClip(ClipData.newPlainText("Tài khoản Garena", text == null ? "" : text));
        }
        @JavascriptInterface public boolean hasOverlayPermission() { return Settings.canDrawOverlays(MainActivity.this); }
        @JavascriptInterface public boolean hasAccessibilityPermission() { return MainActivity.this.hasAccessibility(); }
        @JavascriptInterface public void openPermission(String permission) {
            runOnUiThread(() -> {
                if ("overlay".equals(permission)) startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())));
                else startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            });
        }
        @JavascriptInterface public void checkUpdates() {
            runOnUiThread(() -> {
                TextView status = new TextView(MainActivity.this) {
                    @Override public void setText(CharSequence text, BufferType type) {
                        super.setText(text, type);
                        if (webView != null) {
                            String message = JSONObject.quote(text == null ? "" : text.toString());
                            webView.post(() -> webView.evaluateJavascript("toast(" + message + ")", null));
                        }
                    }
                };
                GithubUpdateChecker.check(MainActivity.this, status);
            });
        }
        @JavascriptInterface public void saveManualTxt(String filename, String content) {
            pendingManualName = filename == null || filename.trim().isEmpty() ? "garena-accounts.txt" : filename;
            pendingManualContent = content == null ? "" : content;
            runOnUiThread(() -> {
                Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TITLE, pendingManualName);
                startActivityForResult(intent, SAVE_FILE);
            });
        }
    }

    private void openGame() {
        Intent launch = getPackageManager().getLaunchIntentForPackage("com.garena.game.kgvn");
        if (launch != null) { launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); startActivity(launch); }
        else if (webView != null) webView.evaluateJavascript("toast('Không tìm thấy Liên Quân trên thiết bị')", null);
    }

    private void syncPermissionStates() {
        if (webView == null) return;
        boolean overlay = Settings.canDrawOverlays(this), accessibility = hasAccessibility();
        String script = "document.querySelectorAll('.permissionState').forEach(b=>{const ok=b.dataset.permission==='overlay'?" + overlay + ":" + accessibility + ";b.textContent=ok?'Đã cấp':'Cần bật';b.classList.toggle('off',!ok)})";
        webView.evaluateJavascript(script, null);
    }
    private boolean hasAccessibility() {
        String target = getPackageName() + "/.GameLoginAccessibilityService";
        String enabled = Settings.Secure.getString(getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        return enabled != null && enabled.toLowerCase().contains(target.toLowerCase());
    }
    @Override protected void onResume() { super.onResume(); if (webView != null) webView.postDelayed(this::syncPermissionStates, 250); }
    @Override protected void onActivityResult(int request, int result, Intent data) {
        super.onActivityResult(request, result, data);
        if (request == PICK_FILE) {
            if (fileChooserCallback != null) {
                Uri[] resultUris = result == RESULT_OK && data != null && data.getData() != null ? new Uri[]{data.getData()} : null;
                fileChooserCallback.onReceiveValue(resultUris);
                fileChooserCallback = null;
            }
        } else if (request == SAVE_FILE && result == RESULT_OK && data != null && data.getData() != null) {
            try (OutputStream out = getContentResolver().openOutputStream(data.getData())) {
                if (out != null) out.write(pendingManualContent.getBytes(StandardCharsets.UTF_8));
                if (webView != null) webView.evaluateJavascript("toast('Đã lưu file TXT')", null);
            } catch (Exception e) { if (webView != null) webView.evaluateJavascript("toast('Không thể lưu tệp TXT')", null); }
        }
    }
    @Override public void onBackPressed() { if (webView != null && webView.canGoBack()) webView.goBack(); else super.onBackPressed(); }
    @Override protected void onDestroy() { if (webView != null) webView.destroy(); super.onDestroy(); }
}
