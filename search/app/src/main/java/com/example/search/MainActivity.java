package com.example.search;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.search.model.Shop;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText etSearch;
    private Button btnSearch, btnExecuteSearch, btnCloseSearch;
    private TextView tvSearchCount, tvResults;
    private LinearLayout llSearchBar;

    private List<Shop> shops; // ショップデータを格納するリスト
    private int searchCount = 10; // 検索可能回数（初期値）
    private boolean isSearchBarVisible = false; // 検索バーの表示状態を管理

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // UI要素の初期化
        etSearch = findViewById(R.id.etSearch);
        btnSearch = findViewById(R.id.btnSearch);
        btnExecuteSearch = findViewById(R.id.btnExecuteSearch);
        btnCloseSearch = findViewById(R.id.btnCloseSearch);
        tvSearchCount = findViewById(R.id.tvSearchCount);
        tvResults = findViewById(R.id.tvResults);
        llSearchBar = findViewById(R.id.llSearchBar);

        initializeData(); // 初期データをセット

        // ボタンのクリックリスナーを設定
        btnSearch.setOnClickListener(v -> toggleSearchBar()); // 検索バー表示/非表示切替
        btnExecuteSearch.setOnClickListener(v -> executeSearch()); // 検索実行
        btnCloseSearch.setOnClickListener(v -> hideSearchBar()); // 検索バーを隠す
        btnSearch.setOnLongClickListener(v -> showSearchCount()); // 検索回数の長押し表示
    }

    // ショップデータを初期化
    private void initializeData() {
        shops = new ArrayList<>();

        // 食事ジャンル
        shops.add(new Shop("食事", "岡山うどん", "カジュアル", "地元の素材を使った美味しいうどん", "岡山市北区", 34.6618, 133.9344, List.of("うどん", "麺類", "食事", "地元料理", "グルメ")));
        shops.add(new Shop("食事", "岡山寿司屋", "高級", "新鮮な魚を使った寿司を提供するお店", "岡山市北区", 34.6635, 133.9294, List.of("寿司", "魚", "和食", "グルメ", "高級")));
        shops.add(new Shop("食事", "岡山ラーメン", "カジュアル", "岡山のご当地ラーメンを楽しめるお店", "岡山市南区", 34.6419, 133.9197, List.of("ラーメン", "食事", "地元料理", "グルメ", "カジュアル")));
        shops.add(new Shop("食事", "岡山ステーキハウス", "高級", "豪華なステーキを提供する高級店", "岡山市北区", 34.6605, 133.9341, List.of("ステーキ", "肉料理", "グルメ", "高級")));
        shops.add(new Shop("食事", "岡山天ぷら屋", "カジュアル", "サクサクの天ぷらを楽しめるお店", "岡山市北区", 34.6630, 133.9350, List.of("天ぷら", "和食", "食事", "カジュアル")));
        shops.add(new Shop("食事", "岡山カフェ", "カジュアル", "オーガニックコーヒーと軽食のカフェ", "岡山市南区", 34.6403, 133.9175, List.of("カフェ", "コーヒー", "食事", "軽食")));
        shops.add(new Shop("食事", "岡山和食屋", "高級", "上質な和食が楽しめる店", "岡山市北区", 34.6640, 133.9300, List.of("和食", "高級", "食事")));
        shops.add(new Shop("食事", "岡山焼肉店", "カジュアル", "地元産のお肉を使った焼肉店", "岡山市北区", 34.6550, 133.9300, List.of("焼肉", "肉料理", "食事")));
        shops.add(new Shop("食事", "岡山カレー屋", "カジュアル", "スパイシーなカレーを提供するお店", "岡山市南区", 34.6430, 133.9200, List.of("カレー", "食事", "スパイシー")));
        shops.add(new Shop("食事", "岡山ビストロ", "カジュアル", "フランス料理をカジュアルに楽しめるお店", "岡山市北区", 34.6615, 133.9310, List.of("フランス料理", "ビストロ", "カジュアル")));

// お土産ジャンル
        shops.add(new Shop("お土産", "岡山名物マスカット", "スイーツ", "岡山の特産品を使ったスイーツ", "岡山市南区", 34.6485, 133.9161, List.of("マスカット", "スイーツ", "お土産", "果物")));
        shops.add(new Shop("お土産", "きびだんご", "スイーツ", "岡山名物きびだんごが楽しめる店", "岡山市北区", 34.6618, 133.9344, List.of("きびだんご", "スイーツ", "お土産", "伝統")));
        shops.add(new Shop("お土産", "岡山バラエティショップ", "雑貨", "岡山のお土産を豊富に取り扱うショップ", "岡山市北区", 34.6615, 133.9300, List.of("雑貨", "お土産", "岡山", "ショップ")));
        shops.add(new Shop("お土産", "岡山デザイン雑貨店", "雑貨", "ユニークなデザインの岡山土産を提供する店", "岡山市南区", 34.6467, 133.9101, List.of("雑貨", "デザイン", "お土産")));
        shops.add(new Shop("お土産", "岡山茶屋", "食品", "岡山産の茶葉を使ったお土産茶葉", "岡山市北区", 34.6600, 133.9300, List.of("お茶", "茶葉", "お土産", "飲料")));
        shops.add(new Shop("お土産", "倉敷陶器店", "陶器", "倉敷で作られた陶器を取り扱う店", "倉敷市", 34.5888, 133.7728, List.of("陶器", "倉敷", "お土産", "伝統")));
        shops.add(new Shop("お土産", "岡山お菓子屋", "スイーツ", "岡山特産のスイーツを販売するお店", "岡山市南区", 34.6450, 133.9150, List.of("お菓子", "スイーツ", "お土産")));
        shops.add(new Shop("お土産", "岡山ジュエリーショップ", "アクセサリー", "岡山産のジュエリーを取り扱うショップ", "岡山市北区", 34.6625, 133.9290, List.of("ジュエリー", "アクセサリー", "お土産")));
        shops.add(new Shop("お土産", "岡山観光土産店", "雑貨", "観光名所の関連商品を販売するお土産店", "岡山市北区", 34.6610, 133.9315, List.of("観光", "雑貨", "お土産")));

// 観光施設ジャンル
        shops.add(new Shop("観光施設", "岡山城", "歴史的観光地", "岡山の歴史を感じる名城", "岡山市北区", 34.6617, 133.9352, List.of("岡山城", "観光地", "歴史", "文化")));
        shops.add(new Shop("観光施設", "後楽園", "庭園", "日本三名園の一つ、広大な庭園でリラックス", "岡山市北区", 34.6544, 133.9367, List.of("後楽園", "庭園", "観光地", "自然")));
        shops.add(new Shop("観光施設", "西大寺", "寺院", "岡山の古刹、西大寺で心を癒す", "岡山市東区", 34.6946, 133.9900, List.of("寺院", "歴史", "観光地")));
        shops.add(new Shop("観光施設", "岡山文化村", "文化施設", "地元のアートを楽しむ文化施設", "岡山市東区", 34.6750, 133.9800, List.of("アート", "文化施設", "観光地")));
        shops.add(new Shop("観光施設", "倉敷美観地区", "歴史的観光地", "伝統的な街並みが残る観光地", "倉敷市", 34.5895, 133.7730, List.of("歴史", "観光地", "美観地区")));
        shops.add(new Shop("観光施設", "吉備路文学館", "文学施設", "吉備の歴史と文学を学べる施設", "岡山市北区", 34.6715, 133.9330, List.of("文学", "歴史", "文化施設")));
        shops.add(new Shop("観光施設", "吉備津神社", "神社", "岡山の古代信仰を学べる神社", "岡山市北区", 34.6620, 133.9315, List.of("神社", "歴史", "観光地")));
        shops.add(new Shop("観光施設", "岡山動物園", "動物園", "多彩な動物たちと触れ合える場所", "岡山市北区", 34.6505, 133.9300, List.of("動物園", "観光地", "家族")));
        shops.add(new Shop("観光施設", "鷲羽山ハイランド", "遊園地", "絶景を楽しめる遊園地", "倉敷市", 34.5850, 133.7410, List.of("遊園地", "観光地", "絶景")));

// イベントジャンル
        shops.add(new Shop("イベント", "岡山花火大会", "年次イベント", "夏の風物詩、迫力のある花火が楽しめる", "岡山市", 34.6618, 133.9344, List.of("花火", "イベント", "夏祭り", "年次イベント")));
        shops.add(new Shop("イベント", "岡山音楽祭", "音楽イベント", "地域のアーティストが集まる音楽フェスティバル", "岡山市", 34.6618, 133.9344, List.of("音楽", "イベント", "フェスティバル", "地域")));
        shops.add(new Shop("イベント", "岡山マラソン", "スポーツイベント", "毎年開催されるマラソン大会", "岡山市", 34.6618, 133.9344, List.of("マラソン", "スポーツ", "イベント", "年次イベント")));
        shops.add(new Shop("イベント", "岡山ビール祭り", "グルメイベント", "地元のクラフトビールと美味しい料理を楽しむイベント", "岡山市", 34.6618, 133.9344, List.of("ビール", "グルメ", "イベント", "地域")));
        shops.add(new Shop("イベント", "岡山映画祭", "映画イベント", "映画の祭典、様々な映画を上映するイベント", "岡山市", 34.6600, 133.9330, List.of("映画", "イベント", "フェスティバル", "文化")));
        shops.add(new Shop("イベント", "岡山ハロウィン祭り", "地域イベント", "岡山で開催されるハロウィンイベント", "岡山市", 34.6618, 133.9344, List.of("ハロウィン", "イベント", "地域")));
        shops.add(new Shop("イベント", "岡山フェスティバル", "年次イベント", "岡山の文化を体験できるイベント", "岡山市", 34.6618, 133.9344, List.of("文化", "イベント", "地域")));
        shops.add(new Shop("イベント", "岡山古本市", "文化イベント", "古本を集めた市場イベント", "岡山市", 34.6618, 133.9344, List.of("本", "イベント", "文化")));
        shops.add(new Shop("イベント", "岡山アートフェア", "アートイベント", "岡山のアーティストが集まるアートイベント", "岡山市", 34.6618, 133.9344, List.of("アート", "イベント", "文化")));
        shops.add(new Shop("イベント", "岡山花の展覧会", "展示イベント", "岡山の美しい花々を展示するイベント", "岡山市", 34.6618, 133.9344, List.of("花", "イベント", "展示")));
    }


        // 入力された検索ワードに関連する類義語を取得
    private List<String> getSynonyms(String query) {
        List<String> synonyms = new ArrayList<>();

        if (query.equals("食事")) {
            synonyms.add("グルメ");
            synonyms.add("料理");
            synonyms.add("食べ物");
            synonyms.add("ランチ");
            synonyms.add("ディナー");
            synonyms.add("軽食");
            synonyms.add("食事処");
            synonyms.add("飲食");
            synonyms.add("食堂");
            synonyms.add("和食");
            synonyms.add("レストラン");
        }
        else if (query.equals("お土産")) {
            synonyms.add("ギフト");
            synonyms.add("記念品");
            synonyms.add("おみやげ");
            synonyms.add("土産物");
            synonyms.add("贈り物");
            synonyms.add("プレゼント");
            synonyms.add("ショップ");
        }
        else if (query.equals("観光施設")) {
            synonyms.add("観光地");
            synonyms.add("名所");
            synonyms.add("観光スポット");
            synonyms.add("旅行先");
            synonyms.add("観光名所");
            synonyms.add("観光地");
            synonyms.add("観光地帯");
        }
        else if (query.equals("イベント")) {
            synonyms.add("祭り");
            synonyms.add("フェスティバル");
            synonyms.add("行事");
            synonyms.add("催し物");
            synonyms.add("活動");
            synonyms.add("イベント会場");
            synonyms.add("特別イベント");
        }

        return synonyms;
    }


    // 検索を実行するメソッド
    private void executeSearch() {
        // 検索回数が残っていない場合はメッセージを表示
        if (searchCount <= 0) {
            tvResults.setText("検索回数が上限に達しました。");
            tvResults.setVisibility(View.VISIBLE);
            return;
        }

        String query = etSearch.getText().toString().trim(); // 検索ワードを取得
        if (query.isEmpty()) { // 検索ワードが空であればエラーメッセージ
            tvResults.setText("検索キーワードを入力してください。");
            tvResults.setVisibility(View.VISIBLE);
            return;
        }

        // 類義語を検索対象に追加
        List<String> searchTerms = new ArrayList<>();
        searchTerms.add(query);
        searchTerms.addAll(getSynonyms(query)); // 類義語も検索ワードに追加

        List<String> results = new ArrayList<>();
        // ショップリストを巡回して検索
        for (Shop shop : shops) {
            for (String term : searchTerms) {
                // 検索ワードとショップ情報が一致する場合
                if (shop.getName().contains(term) || shop.getCategory().contains(term) || shop.getKeywords().contains(term)) {
                    results.add(shop.toString()); // 結果をリストに追加
                    break; // 一度一致したら次の検索語に進む
                }
            }
        }

        // 結果がなければメッセージを表示
        if (results.isEmpty()) {
            tvResults.setText("ヒットしませんでした。");
        } else {
            tvResults.setText("検索結果:\n" + String.join("\n\n", results)); // 検索結果を表示
        }

        tvResults.setVisibility(View.VISIBLE); // 結果表示
        searchCount--; // 検索回数を減らす
        updateSearchCountDisplay(); // 検索回数を更新
    }

    // 検索回数を表示
    private void updateSearchCountDisplay() {
        tvSearchCount.setText("検索可能回数: " + searchCount);
        tvSearchCount.setVisibility(View.GONE); // 長押し時のみ表示
    }

    // 検索バーの表示/非表示を切り替え
    private void toggleSearchBar() {
        if (isSearchBarVisible) {
            hideSearchBar(); // 表示中なら非表示
        } else {
            showSearchBar(); // 非表示なら表示
        }
    }

    // 検索バーを表示
    private void showSearchBar() {
        // アニメーションで検索バーをスライドイン
        TranslateAnimation slideIn = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        slideIn.setDuration(300);
        llSearchBar.startAnimation(slideIn);
        llSearchBar.setVisibility(View.VISIBLE); // 検索バーを表示
        btnSearch.setVisibility(View.GONE); // 検索ボタンを非表示
        isSearchBarVisible = true;
    }

    // 検索バーを非表示
    private void hideSearchBar() {
        // アニメーションで検索バーをスライドアウト
        TranslateAnimation slideOut = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        slideOut.setDuration(200);
        slideOut.setInterpolator(new android.view.animation.DecelerateInterpolator()); // アニメーションのイージング
        llSearchBar.startAnimation(slideOut);
        llSearchBar.setVisibility(View.GONE); // 検索バーを非表示
        btnSearch.setVisibility(View.VISIBLE); // 検索ボタンを再表示
        isSearchBarVisible = false;

        tvResults.setVisibility(View.GONE); // 検索結果を非表示
    }

    // 検索回数を長押しで表示
    private boolean showSearchCount() {
        tvSearchCount.setText("検索可能回数: " + searchCount);
        tvSearchCount.setVisibility(View.VISIBLE); // 表示
        btnSearch.postDelayed(() -> tvSearchCount.setVisibility(View.GONE), 2000); // 2秒後に非表示
        return true; // 長押しアクションを終了
    }
}
