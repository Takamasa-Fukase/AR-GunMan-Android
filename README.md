# AR-GunMan

[AR-GunMan（iOS）](https://github.com/Takamasa-Fukase/AR-GunMan/tree/main)のAndroid版です。

AR部分のみUnity（C#）で実装し、Unity as a Libraryという仕組みを利用してAndroidネイティブアプリ用のプラグインとして書き出しています。

アプリの土台はKotlinでネイティブ実装し、UI構築にはJetpack composeを利用して一部のゲーム画面にUnityで作成したARコンテンツを組み込んで連携させています。

UnityとKotlin（Jetpack compose）との連携実装についての記事も書きました。

宜しければご覧ください。

[UnityとJetpackComposeでウルトラARシューティングゲームを作ろう！（Kotlin、JetpackCompose連携まとめ）](https://zenn.dev/arsaga/articles/ede728a794a553)

# Demo

### Video

https://github.com/user-attachments/assets/31e9a728-249f-4ddd-8d9a-66fb266ec8c8

### Images

![AR-GunMan_store_pic_1](https://github.com/user-attachments/assets/d9dde500-8ef2-4572-b711-02be9490ef40)
![AR-GunMan_store_pic_2](https://github.com/user-attachments/assets/defe4319-ec7c-45d1-94c8-d0f56c2bda92)
![AR-GunMan_store_pic_3](https://github.com/user-attachments/assets/55541cb8-2c7d-4408-9c8c-357cd7494635)
![AR-GunMan_store_pic 4](https://github.com/user-attachments/assets/547ca453-945d-4dc2-96e9-d6cac6795fff)

# Architecture
StateFlow + MVVM

# Author

* Takamasa Fukase (Ultra-Fukase)
* ultrafukase@gmail.com
