package org.travelers.together.wxapi;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.cordova.PluginResult;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.oauth.wechat.WechatOauth;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;

public class WXEntryActivity extends Activity {

	final String TAG = "WXEntryActivity";

	final String APP_ID = "wxb8587d398599a602";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		handleIntent(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {
		SendAuth.Resp resp = new SendAuth.Resp(intent.getExtras());
		SendAuth.Resp r = (SendAuth.Resp) resp;
		switch (r.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			getToken(r.code);
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			WechatOauth.wechat.callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR));
			WXEntryActivity.this.finish();
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			WechatOauth.wechat.callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR));
			WXEntryActivity.this.finish();
			break;
		default:
			WechatOauth.wechat.callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR));
			WXEntryActivity.this.finish();
			break;
		}
		// Toast.makeText(this, result, Toast.LENGTH_LONG).show();
	}

	/**
	 * 换取token
	 * 
	 * @param code
	 */
	public void getToken(String code) {
		String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + APP_ID + "&secret=c89def438ee755365d59f6a20fa5d098&code=" + code + "&grant_type=authorization_code";
		RequestQueue mQueue = Volley.newRequestQueue(getApplicationContext());
		mQueue.add(new JsonObjectRequest(Method.GET, url, null, new Response.Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				Log.d(TAG, response.toString());
				try {
					getUserinfo(response.getString("access_token"), response.getString("openid"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError response) {
				response.printStackTrace();
				// Toast.makeText(WXEntryActivity.this, "error" + response.getMessage(), Toast.LENGTH_LONG).show();
			}
		}));
		mQueue.start();
	}

	/**
	 * 换取用户资料
	 * 
	 * @param token
	 * @param openid
	 */
	public void getUserinfo(String token, String openid) {

		String url = "https://api.weixin.qq.com/sns/userinfo?access_token=" + token + "&openid=" + openid;
		System.out.println(url);
		RequestQueue mQueue = Volley.newRequestQueue(getApplicationContext());

		JsonObjectRequest jsonR = new JsonObjectRequest(Method.GET, url, null, new Response.Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				Log.d(TAG, response.toString());
				String json = response.toString();
				try {
					json = new String(json.getBytes("ISO-8859-1"), "utf-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				try {
					response = new JSONObject(json);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				WechatOauth.wechat.callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, response));
				WXEntryActivity.this.finish();
			}

		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError response) {
				response.printStackTrace();
				// Toast.makeText(WXEntryActivity.this, "error" + response.getMessage(), Toast.LENGTH_LONG).show();
			}
		}) {
			@Override
			public Map<String, String> getHeaders() {
				HashMap<String, String> headers = new HashMap<String, String>();
				headers.put("Accept", "application/json");
				headers.put("Content-Type", "application/json; charset=utf-8");
				return headers;
			}
		};
		mQueue.add(jsonR);
		mQueue.start();
	}

	/**
	 * 超时刷新 超时2小时
	 * 
	 * @param appid
	 * @param refresh_token
	 */
	public void getRefresh_token(String appid, String refresh_token) {
		String url = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=" + appid + "&grant_type=refresh_token&refresh_token=" + refresh_token;
		RequestQueue mQueue = Volley.newRequestQueue(getApplicationContext());
		mQueue.add(new JsonObjectRequest(Method.GET, url, null, new Response.Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				Log.d(TAG, response.toString());
				// Toast.makeText(WXEntryActivity.this, response.toString(), Toast.LENGTH_LONG).show();
			}

		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError response) {
				response.printStackTrace();
				// Toast.makeText(WXEntryActivity.this, "error" + response.getMessage(), Toast.LENGTH_LONG).show();
			}
		}));
		mQueue.start();
	}

}