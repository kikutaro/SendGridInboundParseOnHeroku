# SendGridのInbound Parse Webhookを利用したサンプル

SendGridのInbound Parse Webhook機能でメール受信したデータをHerokuのアプリへPostします。
Heroku上のJava(Spark)にてメールデータに対して以下の処理を行います。
- Microsoftの[Cognitive Service](https://www.microsoft.com/cognitive-services/en-us/apis)にある[Text Analytics API](https://www.microsoft.com/cognitive-services/en-us/text-analytics-api)を利用し、メール文面のNegative/Positive度合い(Sentimentと呼ばれる)を解析する
- [Plotly](https://plot.ly/)を利用してグラフ化する
- SendGridを利用して、グラフ画像を送信元に返信する

### 実行イメージ

SendGridのInbound Parse Webhookで設定したドメインを含む宛先へメールを送信します。
（Text Analytics APIは2016/11/11現在、日本語に対応していないため、メール内容は英語で送信します）

<img src="https://sendgrid.kke.co.jp/blog/wp/wp-content/uploads/2016/11/Blog2.jpg" alt="送信メール" title="送信メール" width="450px">

送信したメール内容に基づいて、ポジティブ・ネガティブの判定がグラフで返信されます。

<img src="https://sendgrid.kke.co.jp/blog/wp/wp-content/uploads/2016/11/Blog3.jpg" alt="結果メール" title="結果メール" width="450px">

### Heroku Config Vars
実際に同じことを試す場合には、Herokuで以下の環境変数設定が必要です。

| config vars | 値の説明 |
|:-----------|:------------|
| MS_CS_TEXT_ANALYTICS_API_KEY | Microsoft Cognitive ServiceのText Analytics API用キー|
| PLOTLY_USER_ID| plot.lyのユーザID|
| PLOTLY_PASSWORD| plot.lyのパスワード|
| SG_API_KEY| SendGridのAPI Key|
