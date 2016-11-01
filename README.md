# SendGridのInbound Parse Webhookを利用したサンプル

SendGridのInbound Parse Webhook機能でメール受信したデータをHerokuのアプリへPostします。
Heroku上のJava(Spark)にてメールデータに対して以下の処理を行います。
- Microsoftの[Cognitive Service](https://www.microsoft.com/cognitive-services/en-us/apis)にある[Text Analytics API](https://www.microsoft.com/cognitive-services/en-us/text-analytics-api)を利用し、メール文面のNegative/Positive度合い(Sentimentと呼ばれる)を解析する
- [Plotly](https://plot.ly/)を利用してグラフ化する
- SendGridを利用して、グラフ画像を送信元に返信する

### Heroku Config Vars
| config vars | 値の説明 |
|:-----------|:------------|
| MS_CS_TEXT_ANALYTICS_API_KEY | Microsoft Cognitive ServiceのText Analytics API用キー|
| PLOTLY_USER_ID| plot.lyのユーザID|
| PLOTLY_PASSWORD| plot.lyのパスワード|
| SG_API_KEY| SendGridのAPI Key|
