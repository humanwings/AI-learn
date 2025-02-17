| 要素（シート） | 観点 | チェック項目 | 期待値 |
| --- | --- | --- | --- |
| コードフォーマット | import文に過不足がないか | import文の編成（Ctrl+Shift+O）を適用し、import文の増減がないかを確認する | - |
| コードフォーマット | import文で*（ワイルドカード）を使っていないか | import文で*（ワイルドカード）を使っていないか | - |
| コードフォーマット | 同一グループ内でアルファベット順にソート | 同じパッケージのimportは隣接させる | - |
| コードフォーマット | 文字コード | UTF-8(MOBなし) | - |
| コーディング規約 | 変数名 | 変数に意味のわかる名前をつけているか | - |
| コーディング規約 | メソッド名 | メソッド名が「動詞＋対象」の形式になっているか | - |
| コーディング規約 | コメント | コメントに理解が難しい内容、およびその処理を明確にする内容が記載されているか | - |
| コーディング規約 | コメント | javadocコメントに「author」を記載しないこと | - |
| コーディング規約 | 型の宣言 | 型の宣言で「var」を使用していないこと | - |
| ガイドライン | クエリで条件として指定できるデータ構造と、APIで返却するデータの構造が一致しているか | 上位互換を保つ設計をする例）BLコードガイドマスタ検索(整合性優先)public Page<? extends PmBlCdGuide> findProc(Query<PmBlCdGuide> query) {    List<PmBlCdGuide> blCodeSettingFind = getPmBlCdGuideProc(query, FIND_PROC);    Page<? extends PmBlCdGuide> page = PagableUtils.toPage(blCodeSettingFind, query.getPageRequest());    return page;  } | - |
| ガイドライン | ひとつのリソースに対する検索、登録、更新、削除といった要求に対するエンドポイントが１つとなっているか⇒検索API以外に、重複チェックAPIを作るなど同じ検索処理で複数のAPIを作成していないか | エンドポイントを乱立させない悪い事例）①独自のエンドポイントのAPI　従業員は、コードでしか取得しないため、API方式仕様ではなく、　下記のようなURLにした。　/api/v1/employee/common/code/{code}　⇒名称での検索が必要となった時、別途エンドポイントを作成　　する必要がある。複数条件での検索を行う対応をした結果、　　同一の機能仕様を持つAPIが複数でき、どれを利用してよい　　かわからなくなる。②“機能”だけを考えたAPI　車台番号の重複チェックのため、車台番号重複チェックAPIを開発した。　⇒車両の検索をするだけのAPIであり、本来は車両検索APIで　　車台番号で取得するようにしなければならない。複数条件で　　の検索を行う対応をした結果、同一の機能仕様を持つAPIが　　複数でき、どれを利用してよいかわからなくなる。不必要に　　IN/OUTの設計が必要となり、生産性が低下する。 | - |
| ガイドライン | Cassandraへのアクセスを基板部品を使わずに行っていないか⇒パッケージjp.co.broadleaf.blcloud配下にないDBアクセス用のクラスがimportされ、使用されていないか | 独自のAPIを作らない悪い事例）①Spring、JAX-RS仕様のAPI　データモデルの設計が納期に間に合わないため、POJOを　INPUT、OUTPUTとするAPIを作成した。　⇒JSONの出力仕様を基盤部品で改修する際、対象APIだけ　　独自にserialize処理を実装しなければならなくなる。②データストアに直接アクセスするAPI　Cassandraへのアクセスを基盤部品を介せず、直接Cassandraの　APIを利用して参照するようにした。　⇒cassandraのアクセス仕様は基盤側で変わるため、独自実装　　部分が正常に実行できなくなる | - |
| ガイドライン | APIが単機能になっているか⇒取得、検索APIにもかかわらず追加や更新を行っているなど、APIの操作にそぐわない処理が含まれていないか | 簡単なロジックを保つこと悪い事例）①UI入力順序も意識する複雑なAPI　画面の入力状態を保存するAPIを作る際、1回の呼び出しに　おいて、金額計算を行うための入力順序を持たせるようにした。　⇒画面の入力仕様を全て　【 保存するAPI 】　で責務を担保　　しなくてはならなくなる。②UIにおける処理の自動化まで行ったAPI　画面表示時、下書き情報が存在した場合、検索APIで自動削除　するようにした。　⇒本来取り合える選択肢である　　　・下書き状態のマージ　　　・削除する　　　・上書きする　　において、UIが発生するとAPIまで影響が及んでしまう。 | - |
| ガイドライン | フロントエンドに依存したAPIになっていないか⇒特定のフロントエンド処理に合わせたI/Fとするのではなく、どのフロントエンド処理からも呼び出せるような汎用的なI/Fになっているか | フロントエンドに依存しない悪い事例）　①UIの導線を促すメッセージを返すAPI　　エラー発生時、UIの画面遷移を促すメッセージを返すように　　した。　　⇒UI側の動きが変わるたびにAPI側のメッセージ変更が必要　　　となる。APIエコノミーではなくなってしまう。 | - |
| ガイドライン | 基盤の内部部品（XXXFactory） | 基盤の内部部品（XXXFactory）を利用していないか | - |
| ガイドライン | メッセージ | メッセージをstatic定数でJavaクラスにハードコードしていないか | DD採番したものを使用する |
| ガイドライン | ログ | ログ出力時System.outを使用しないこと | - |
| ガイドライン | Query | Query文字列を作成する際StringBuilderを使用していないか | QueryJavaBuilderを使用すること |
| ガイドライン | 同一ドメイン内の他のAPIを直接インスタンス化して呼び出していないか⇒newしている箇所の中からAPIのインスタンスを作成している箇所がないか調べる | ・同一ドメイン内の場合は、JavaApiExecuterを使用して呼び出す・異なるドメインの場合は、Web API経由（RestTemplateExecuterを使用して）で呼び出す | - |
| ガイドライン | 同一ドメイン内の他のAPIを直接DIして呼び出していないか | ・同一ドメイン内の場合は、JavaApiExecuterを使用して呼び出す・異なるドメインの場合は、Web API経由（RestTemplateExecuterを使用して）で呼び出す | - |
| ガイドライン | 文字列計算にStringUtilsを使用しているか | 文字列計算にStringUtilsを使用しているか | - |
| ガイドライン | 基盤のライブラリで置き換えられる処理がないか⇒JavaDocを参照し、使えそうなライブラリがないか確認する | 例えば、消費税算出、日付関連処理などは、基盤にライブラリが存在するため、そちらを使用する | - |
| 一般 | 設計書に記載されている処理が、漏れなく実装されているか。 | 検索の機能性優先は、基盤のsearchメソッドを使用する。整合性優先は、基盤のfindメソッドを使用する。⇒入力パラメータの整合性モデルの意味は以下の通り。　・強整合性・・・常に最新の状態が参照されるようにする。　・結果整合性・・・内部でエラーが発生している状態を除き、結果を書き込み即座に更新結果を参照する事ができる。　・結果整合性(弱）・・・1分以上のタイムラグを許容できる。 | - |
| 一般 | 分岐条件が複雑になっていないか | 分岐条件が複雑になっていないか | - |
| 一般 | ネストが深くなっていないか | ネストが深くなっていないか | - |
| 一般 | ループ回数、またはループの終了条件は正しいか | ループ回数、またはループの終了条件は正しいか | - |
| 一般 | 同じ処理を複数箇所で実装していないか | メソッドに切り出したほうがよいコードを抽出する | - |
| 一般 | コメントアウトされたままのコードがないか | コメントアウトされたままのコードがないか | - |
| 一般 | デバッグ用のコードなど、不要なコードがないか | デバッグ用のコードなど、不要なコードがないか | - |
| 一般 | TODO、FIXMEがないか | TODO、FIXMEがないか | - |
| 一般 | 既存ソースに修正を加えている場合、既存処理に影響を与えていないか。 | 既存ソースに修正を加えている場合、既存処理に影響を与えていないか。 | - |
| 一般 | Java,Spring,broadlef以外のライブラリを使用している箇所がないか。 | import文にて、パッケージ名を確認する | - |
| 一般 | 他ドメインのAPIコールは同一ドメイン内で1か所となっていること | RestTemplateExecuterでコールしていること | - |
| 一般 | API呼び出しの実装が正しいこと※異なるドメイン | 検索(機能性優先)API | // 検索(機能性優先)APISupplierInfo supplierInfo = new SupplierInfo();ExecuterSearchResponse<SupplierInfo[]> response = restTemplateExecuter.search(supplierInfo.getSubDomain(),    supplierInfo.getSubjectPathAsUrl(), builder.toString(), SupplierInfo[].class); |
| 一般 | API呼び出しの実装が正しいこと※異なるドメイン | 検索(整合性優先)API | // 検索(整合性優先)APISupplierInfo supplierInfo = new SupplierInfo();ExecuterSearchResponse<SupplierInfo[]> response = restTemplateExecuter.find(supplierInfo.getSubDomain(),    supplierInfo.getSubjectPathAsUrl(), builder.toString(), SupplierInfo[].class); |
| 一般 | API呼び出しの実装が正しいこと※異なるドメイン | 作成API | // 作成API      this.restTemplateExecuter.execute(supplierDesignPaySlipInfo.getSubDomain(), draftInfo.getEntityIdPathAsUrl(),        HttpMethod.POST, draftInfo, DraftSupplierDesignPaySlipInfo.class); |
| 一般 | API呼び出しの実装が正しいこと※異なるドメイン | 更新API | // 更新APIrestTemplateExecuter.execute(entity.getSubDomain(), entity.getEntityIdPathAsUrl(), HttpMethod.PUT, entity,        TaxFractionProcSetting.class); |
| 一般 | API呼び出しの実装が正しいこと※異なるドメイン | 削除API | // 削除APIrestTemplateExecuter.execute(entity.getSubDomain(), entity.getEntityIdPathAsUrl(), HttpMethod.DELETE, null,        UserEmployeeConnect.class); |
| 一般 | API呼び出しの実装が正しいこと※異なるドメイン | 取得API | // 取得APISupplierInfo supplierInfo = new SupplierInfo();supplierInfo.setSupplierCd(123);ExecuterResponse<SupplierInfo> response = this.restTemplateExecuter.get(supplierInfo.getSubDomain(),          supplierInfo.getEntityIdPathAsUrl(), SupplierInfo.class); |
| 一般 | API呼び出しの実装が正しいこと※異なるドメイン | 拡張API | API_IF仕様書(BD-23)と一致すること。 |
| その他 | 検索系はfパラを指定し必要最小限の項目だけ取得している事 | 検索(機能性優先)API/検索(整合性優先)API | - |
| その他 | 検索系はsを必ず指定し、デフォルト10サイズしか検索しない不具合がないことを確認 | 検索(機能性優先)API/検索(整合性優先)API | - |
| その他 | 無駄にループして複数回同じAPIやテーブルへアクセスしていない事を確認 | 無駄のループなし | - |
| その他 | ALLOW FILTERINGがついている場合、PartitionKeyは全て指定されている事を確認 | 検索(整合性優先)API | - |
| その他 | トランザクションをかけている場合、トランザクション内で同一レコードへのDB操作をしていない事を確認 | トランザクション | - |
| その他 | ソースの未使用項目は削除済みである | 未使用項目 | - |
| その他 | 本処理にサブジェクト固有の処理を実装していない事を確認 | サブジェクト | - |
| その他 | 非同期処理をしている場合、内部でさらに非同期処理が行われていない事を確認 | 非同期処理 | - |
| その他 | メソッド間のデータ共有で、クラスメンバ変数を利用していないことを確認 | クラスメンバ変数 | - |
