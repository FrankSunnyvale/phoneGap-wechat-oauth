# phoneGap-wechat-oauth
phoneGap wechat oauth


## Using the plugin

Example - get user info
```javascript
WechatOauth.getUserInfo({}, function() {
      console.log("ok");
}, function() {
    console.log("error");
});
```