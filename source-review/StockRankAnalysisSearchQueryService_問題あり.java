//****************************************************************************//
// システム         : BL.Cloud
//----------------------------------------------------------------------------//
//                (c)Copyright  2017 Broadleaf Co.,Ltd.
//============================================================================//
package jp.co.broadleaf.blcloud.monthlytally.api;

import static jp.co.broadleaf.blcloud.bizcmn.entity.PropertyDictionary.BL_CD_GROUP_CODE;
import static jp.co.broadleaf.blcloud.bizcmn.entity.PropertyDictionary.BL_PRTS_CD;
import static jp.co.broadleaf.blcloud.bizcmn.entity.PropertyDictionary.DISPLAY_ITEM_MAKER_CD;
import static jp.co.broadleaf.blcloud.bizcmn.entity.PropertyDictionary.ITEM_L_CLASS_CD;
import static jp.co.broadleaf.blcloud.bizcmn.entity.PropertyDictionary.ITEM_L_CLASS_NAME;
import static jp.co.broadleaf.blcloud.bizcmn.entity.PropertyDictionary.ITEM_MAKER_CD;
import static jp.co.broadleaf.blcloud.bizcmn.entity.PropertyDictionary.ITEM_M_CLASS_CD;
import static jp.co.broadleaf.blcloud.bizcmn.entity.PropertyDictionary.ITEM_M_CLASS_NAME;
import static jp.co.broadleaf.blcloud.bizcmn.entity.PropertyDictionary.ITEM_PARTS_NUMBER;
import static jp.co.broadleaf.blcloud.bizcmn.entity.PropertyDictionary.MAX_STOCK_QUANTITY;
import static jp.co.broadleaf.blcloud.bizcmn.entity.PropertyDictionary.MIN_STOCK_QUANTITY;
import static jp.co.broadleaf.blcloud.bizcmn.entity.PropertyDictionary.NUMBER_DESIGN_END;
import static jp.co.broadleaf.blcloud.bizcmn.entity.PropertyDictionary.NUMBER_DESIGN_START;
import static jp.co.broadleaf.blcloud.bizcmn.entity.PropertyDictionary.PURCHASE_STOCK_QUANTITY;
import static jp.co.broadleaf.blcloud.bizcmn.entity.PropertyDictionary.SHELF_NUM;
import static jp.co.broadleaf.blcloud.bizcmn.entity.PropertyDictionary.STOCK_CREATE_DATE;
import static jp.co.broadleaf.blcloud.bizcmn.entity.PropertyDictionary.STOCK_MGT_DIV_CD_LIST;
import static jp.co.broadleaf.blcloud.bizcmn.entity.PropertyDictionary.SUPPLIER_CD;
import static jp.co.broadleaf.blcloud.bizcmn.entity.PropertyDictionary.SUPPLIER_CD_END;
import static jp.co.broadleaf.blcloud.bizcmn.entity.PropertyDictionary.SUPPLIER_CD_START;
import static jp.co.broadleaf.blcloud.bizcmn.entity.PropertyDictionary.WH_CODE;
import static jp.co.broadleaf.blcloud.bizcmn.entity.PropertyDictionary.WH_ORGANIZATION_CODE;
import static jp.co.broadleaf.blcloud.company.entity.PropertyDictionary.ORGANIZATION_CODE;
import static jp.co.broadleaf.blcloud.customer.entity.PropertyDictionary.SUPPLIER_ADDR_NAME;
import static jp.co.broadleaf.blcloud.lang.entity.PropertyDictionary.BL_TENANT_ID;
import static jp.co.broadleaf.blcloud.lang.entity.PropertyDictionary.CREATE_DATE_TIME;

import jp.co.broadleaf.blcloud.api.RestfulQueryService;
import jp.co.broadleaf.blcloud.bizcmn.entity.BlCdGroupCodeSetting;
import jp.co.broadleaf.blcloud.bizcmn.entity.BlCodeSetting;
import jp.co.broadleaf.blcloud.bizcmn.entity.PmItemInfo;
import jp.co.broadleaf.blcloud.bizcmn.entity.StockInfo;
import jp.co.broadleaf.blcloud.bizcmn.entity.SupplierMgtSetting;
import jp.co.broadleaf.blcloud.common.util.DateTimeUtils;
import jp.co.broadleaf.blcloud.lang.db.DbQuery;
import jp.co.broadleaf.blcloud.lang.dto.Query;
import jp.co.broadleaf.blcloud.lang.entity.EntityObject;
import jp.co.broadleaf.blcloud.lang.entity.FileManageInfo;
import jp.co.broadleaf.blcloud.lang.entity.PropertyDefine;
import jp.co.broadleaf.blcloud.lang.vo.BlCloudDate;
import jp.co.broadleaf.blcloud.lang.vo.BlTenantId;
import jp.co.broadleaf.blcloud.monthlytally.api.biz.BizBlCodeSetting;
import jp.co.broadleaf.blcloud.monthlytally.api.biz.StockRankAnalysisSearchBizLogic;
import jp.co.broadleaf.blcloud.monthlytally.api.util.Constant;
import jp.co.broadleaf.blcloud.monthlytally.api.util.ExportCsvCommonService;
import jp.co.broadleaf.blcloud.monthlytally.api.util.ExportCsvService;
import jp.co.broadleaf.blcloud.monthlytally.api.util.PropertyDefineNeedFormat;
import jp.co.broadleaf.blcloud.monthlytally.entity.StockRankAnalysisResult;
import jp.co.broadleaf.blcloud.monthlytally.entity.StockRankAnalysisSearch;
import jp.co.broadleaf.blcloud.monthlytally.vo.AnalysisReportFormDiv;
import jp.co.broadleaf.blcloud.monthlytally.vo.StockRankAnalysisOutputOrderDiv;
import jp.co.broadleaf.blcloud.monthlytally.vo.StockRankAnalysisOutputOrderType;

import jp.co.broadleaf.blcloud.common.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jp.co.broadleaf.blcloud.stock.entity.StockHistoryInfo;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <pre>
 * 在庫順位分析_テキスト出力API
 * </pre>
 * @author YourName
 */
@RestController
@RequestMapping("/api/" + StockRankAnalysisSearchQueryService.VERSION
    + StockRankAnalysisSearchQueryService.RESOURCE_NAME)
public class StockRankAnalysisSearchQueryService extends RestfulQueryService<StockRankAnalysisSearch> {

  /** リソース名。 */
  public static final String RESOURCE_NAME = "/stockrankanalysissearch";

  /** バージョン。 */
  public static final String VERSION = "v1";

  /** DBクエリ。 */
  @Autowired
  protected DbQuery dbQuery;

  /** BLコード設定*/
  @Autowired
  private BizBlCodeSetting bizBlCodeSetting;

  /** CSV出力コモンクラス */
  @Autowired
  private ExportCsvCommonService commonService;

  /** CSV出力API */
  @Autowired
  private ExportCsvService exportCsv;

  /** 在庫順位分析の論理クラス */
  @Autowired
  private StockRankAnalysisSearchBizLogic stockLogic;

  /**
   * L-Monthlytally-0015 在庫順位分析_テキスト出力
   * @param subject URLパス経由で取得
   * @param allRequestParams 画面から送信された条件を含むマップ
   * @return ResponseEntity ファイル管理情報
   * @throws IOException IOException
   */
  @RequestMapping(value = "/{subject:[a-zA-Z\\-_]+}/csv", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> exportCsvStockRankAnalysis(@PathVariable String subject,
                                                           @RequestParam Map<String, String> allRequestParams) throws IOException {
    Query<StockRankAnalysisSearch> query = this.createQuery(subject, allRequestParams, appendOpFunction());
    // 1.在庫履歴情報_検索(機能性優先)のINPUT情報用意
    StockRankAnalysisSearch stockRankQuery = (StockRankAnalysisSearch) this.commonService
        .convertConditionToObject(query);

    Map<PropertyDefine<?>, Boolean> mapCheckExistsCondition = this.commonService.createMapCheckExistsCondition(query);

    this.runBeforeFeatures("csv", subject, stockRankQuery);
    List<StockRankAnalysisResult> resultList = new ArrayList<>();
    // 2.在庫履歴情報取得
    List<StockHistoryInfo> stockHistoryList = this.stockLogic.searchStockHistoryInfo(stockRankQuery, mapCheckExistsCondition);
    if (CollectionUtils.isNotEmpty(stockHistoryList)) {
      // 4.在庫情報取得
      List<StockInfo> stockInfoList = this.stockLogic.searchStockInfo(stockRankQuery, mapCheckExistsCondition);
      // 倉庫コード＋":"＋表示用商品メーカーコード＋":"＋商品品番をキー、
      // BLテナントID＋":"＋組織コード＋":"＋棚番＋":"＋最終仕入日＋":"
      // ＋仕入在庫数＋":"＋最低在庫数＋":"＋最高在庫数を値としたマップに格納する。(B)

      // 6.部品商商品情報取得
      List<PmItemInfo> pmItemInfoList = this.stockLogic.searchPmItemInfo(stockRankQuery, stockHistoryList, mapCheckExistsCondition);

      // 8.BLコード設定取得
      // BLコード設定_検索(機能性優先)＜L-Bizcmn-9130＞：検索を呼び、BLコード設定を取得する。
      List<BlCodeSetting> blCodeSettingList = bizBlCodeSetting.searchBlCodeSetting(stockRankQuery, mapCheckExistsCondition);
      // BL商品コード＋":"＋BLグループコードをキー、BLテナントID＋":"＋BLグループ名称を値としたマップに格納する。 ... (D)
      Map<Integer, String> blCdSettingMap = this.convertBlCodeSettingListToMap(blCodeSettingList);

      // 9.BLコードグループコード設定_検索(機能性優先)のINPUT情報用意
      // 10.BLコードグループコード設定取得
      List<BlCdGroupCodeSetting> blCdGroupCodeSettingList = this.stockLogic.searchBlGroupCode(stockRankQuery, mapCheckExistsCondition);

      // 12.仕入先管理情報取得
      List<SupplierMgtSetting> supplierMgtSettingList = this.searchSupplierMgtSetting(stockHistoryList, pmItemInfoList,
          blCdGroupCodeSettingList, blCdSettingMap);

      // 13.上記取得された在庫履歴情報(A)、在庫情報(B)、部品商商品情報(C)、BLコード設定(D)、BLコードグループコード設定(E)に基づいて、在庫履歴情報(A)の明細件数より以下処理１を行って、在庫順位分析結果配列を生成し値を設定する。
      // 在庫履歴情報(A)項目の設定
      Map<String, StockRankAnalysisResult> stockRankAnalysisResultMap = this.getStockRankAnalysisResult(stockHistoryList,
          stockInfoList, pmItemInfoList, blCodeSettingList, blCdGroupCodeSettingList);

      // 14.上記取得された仕入先管理情報(F)に基づいて、絞り込まれた在庫情報(A)の明細件数より、在庫順位分析結果配列を生成し値を設定する。
      resultList = this.stockRankAnalysis(stockRankAnalysisResultMap,
          supplierMgtSettingList);

      // 16.在庫順位分析検索より、在庫順位分析結果配列を絞り込む。以下の条件を満たす仕入先管理情報が存在しない場合、在庫順位分析結果配列から除く。
      // 在庫順位分析出力結果配列.仕入先コード >= 在庫順位分析検索用情報.仕入先コード(開始)
      if (mapCheckExistsCondition.get(SUPPLIER_CD_START)) {
        resultList.removeIf(n -> n.getSupplierCd() < stockRankQuery.getSupplierCdStart());
      }
      // 在庫順位分析出力結果配列.仕入先コード <= 在庫順位分析検索用情報.仕入先コード(終了)
      if (mapCheckExistsCondition.get(SUPPLIER_CD_END)) {
        resultList.removeIf(n -> n.getSupplierCd() > stockRankQuery.getSupplierCdEnd());
      }
      // 在庫順位分析出力結果配列.出荷数量 >= 在庫順位分析検索用情報.個数指定(開始)
      if (mapCheckExistsCondition.get(NUMBER_DESIGN_START)) {
        resultList.removeIf(n -> n.getShippingQuantity().compareTo(new BigDecimal(stockRankQuery.getNumberDesignStart())) < 0);
      }
      // 在庫順位分析出力結果配列.出荷数量 <= 在庫順位分析検索用情報.個数指定(終了)
      if (mapCheckExistsCondition.get(NUMBER_DESIGN_END)) {
        resultList.removeIf(n -> n.getShippingQuantity().compareTo(new BigDecimal(stockRankQuery.getNumberDesignEnd())) > 0);
      }

      // 17.３つの順位項目を設定する。
      sortRankAnalysisList(stockRankQuery, resultList);

      // 19.在庫順位分析検索用情報より、在庫順位分析出力結果配列を絞り込む。以下の条件を満たさない場合、在庫順位分析出力結果配列から除く。
      if (StockRankAnalysisOutputOrderType.SalesPrice.equals(stockRankQuery.getStockRankAnalysisOutputOrderType())) {
        // 在庫順位分析検索用情報.在庫順位分析出力順種別が「0:売上金額順」の場合
        resultList.removeIf(n -> n.getSalesPriceRank() > stockRankQuery.getStockRankAnalysisOutputOrder());
      } else if (StockRankAnalysisOutputOrderType.GrossProfit
          .equals(stockRankQuery.getStockRankAnalysisOutputOrderType())) {
        // 在庫順位分析検索用情報.在庫順位分析出力順種別が「1:粗利額順」の場合
        resultList.removeIf(n -> n.getGrossProfitRank() > stockRankQuery.getStockRankAnalysisOutputOrder());
      } else if (StockRankAnalysisOutputOrderType.Shipments
          .equals(stockRankQuery.getStockRankAnalysisOutputOrderType())) {
        // 在庫順位分析検索用情報.在庫順位分析出力順種別が「2:出荷数順」の場合
        resultList.removeIf(n -> n.getShippingQuantityRank() > stockRankQuery.getStockRankAnalysisOutputOrder());
      }
    }

    StockRankAnalysisOutputOrderType outputTypeDiv = stockRankQuery.getStockRankAnalysisOutputOrderType();
    // ヘッダ情報リストを取得する。
    LinkedHashMap<PropertyDefine<?>, String> mapHeader = commonService.getListHeaderInfo(AnalysisReportFormDiv.StockRankAnalysis,
        outputTypeDiv, stockRankQuery.getStockRankAnalysisOutputTypeDiv());
    List<String> listHeader = new ArrayList<String>(mapHeader.values());
    // CSVファイルデータを取得する。
    List<List<String>> listData = commonService.getListData(resultList, createListProNeedFormat(), mapHeader);
    // ファイル名
    var fileName = this.commonService.createFileName(AnalysisReportFormDiv.StockRankAnalysis.getName(),
        stockRankQuery.getStockRankAnalysisOutputTypeDiv().getName());
    // CSVファイルを作成します。
    var filePath = this.exportCsv.exportCsv(fileName, listHeader, listData);
    // ファイル管理情報を取得する。
    FileManageInfo fileManageInfo = this.commonService.saveFile(filePath, this.getBlTenantId());

    // ファイルダウンロード情報のレスポンス
    return new ResponseEntity<>(this.serialize(fileManageInfo), HttpStatus.OK);
  }

  /**
   * <pre>
   * stockRankAnalysis
   * </pre>
   * @param stockRankAnalisysResultMap stockRankAnalisysResultMap
   * @param supplierMgtSettingList supplierMgtSettingListト
   * @return 在庫入出荷一覧表出力結果リスト
   */
  private List<StockRankAnalysisResult> stockRankAnalysis(Map<String, StockRankAnalysisResult> stockRankAnalisysResultMap,
                                                                        List<SupplierMgtSetting> supplierMgtSettingList) {
    // 15 上記取得された仕入先管理情報(F)に基づいて、絞り込まれた在庫情報(A)の明細件数より、在庫入出荷一覧表情報を生成し値を設定する F
    List<StockRankAnalysisResult> stockRankAnalysisResultList = new ArrayList<>();
    Map<String, String> supplierMgtSettingMap = this.convertSupplierMgtSettingListToMap(supplierMgtSettingList);
    stockRankAnalisysResultMap.forEach((key, value) -> {
      StockRankAnalysisResult stockRankAnalysisResult = new StockRankAnalysisResult();
      String[] keyCompareArray = key.split(",");
      String keyFilter = keyCompareArray[1];
      if (supplierMgtSettingMap.containsKey(keyFilter)) {
        stockRankAnalysisResult = value;
        String[] supplierInfo = supplierMgtSettingMap.get(keyFilter).split(Constant.SEPARATER);
        stockRankAnalysisResult.setSupplierCd(Integer.parseInt(supplierInfo[0]));
        stockRankAnalysisResult.setSupplierAddrName(supplierInfo[1]);
        stockRankAnalysisResultList.add(stockRankAnalysisResult);
      }
    });
    return stockRankAnalysisResultList;
  }

  /**
   *
   * 上記取得された在庫履歴情報(A)、在庫情報(B)、部品商商品情報(C)、BLコード設定(D)、
   * BLコードグループコード設定(E)に基づいて、在庫履歴情報(A)の明細件数より以下処理１を行って、
   * 在庫順位分析出力結果配列を生成し値を設定する。
   * @param stockHistoryInfoList 在庫履歴情報(A)
   * @param stockInfoList 在庫情報(B)
   * @param pmItemInfoList 部品商商品情報(C)
   * @param blCodeSettingList BLコード設定(D)
   * @param blCdGroupCodeSettingList BLコードグループコード設定(E)
   * @return 在庫入出荷一覧表出力結果地図
   */
  private Map<String, StockRankAnalysisResult> getStockRankAnalysisResult(List<StockHistoryInfo> stockHistoryInfoList,
                                                                          List<StockInfo> stockInfoList,
                                                                          List<PmItemInfo> pmItemInfoList,
                                                                          List<BlCodeSetting> blCodeSettingList,
                                                                          List<BlCdGroupCodeSetting> blCdGroupCodeSettingList) {
    // 出荷と到着の価値を高めるための在庫履歴情報のマップ
    Map<String, StockRankAnalysisResult> stockRankAnalysisResultMap = new HashMap<>();
    // 在庫情報項目の設定
    Map<String, String> stockInfoMap = this.convertStockInfoListToMap(stockInfoList);
    // 部品商商品情報項目の設定
    Map<String, Integer> pmItemInfoMapByStockInfo = this.convertPmItemInfoListToMapBySI(pmItemInfoList);
    // BLコード情報項目の設定
    Map<String, BlTenantId> pmItemInfoMapByBlCodeSetting = this
        .convertPmItemInfoListToMapByBlCodeSetting(pmItemInfoList);
    // BLコードグループコード情報(E)項目の設定
    Map<String, String> blCdGroupCodeSettingMap = this.convertBlCdGroupCodeSettingListToMap(blCdGroupCodeSettingList);
    stockHistoryInfoList.forEach(stockHistoryInfo -> {
      blCodeSettingList.forEach(blCodeSetting -> {
        StockRankAnalysisResult stockRankAnalysisResult = new StockRankAnalysisResult();
        String keyCompare1 = getKeyCompare(stockHistoryInfo, Constant.KEY_COMPARE_STOCK_INFO);
        String keyCompare2 = getKeyCompare(stockHistoryInfo, Constant.KEY_COMPARE_PM_ITEM_BY_STOCK);
        String keyCompare3 = getKeyCompare(blCodeSetting, Constant.KEY_COMPARE_PM_ITEM_BY_BL_PRTS_CD);
        String keyCompare4 = getKeyCompare(blCodeSetting, Constant.KEY_COMPARE_BL_CD_GROUP_CODE);
        String keyCompare5 = getKeyCompare(stockHistoryInfo, Constant.KEY_COMPARE_SUPPLIER);
        String keyOutPutResultFilter = null;
        StringBuilder keyStringBuilder = new StringBuilder();
        if (stockInfoMap.containsKey(keyCompare1) && pmItemInfoMapByStockInfo.containsKey(keyCompare2)
            && pmItemInfoMapByBlCodeSetting.containsKey(keyCompare3)
            && blCdGroupCodeSettingMap.containsKey(keyCompare4)) {
          // 倉庫コード = (A).倉庫コード
          stockRankAnalysisResult.setWhCode(stockHistoryInfo.getWhCode());
          // 倉庫名称 = (A).倉庫名称
          stockRankAnalysisResult.setWhName(stockHistoryInfo.getWhName());
          // 商品メーカー全角名称 = (A).商品メーカー全角名称
          stockRankAnalysisResult.setItemMakerFullWName(stockHistoryInfo.getItemMakerFullWName());
          // 商品品番 = (A).商品品番
          stockRankAnalysisResult.setItemPartsNumber(stockHistoryInfo.getItemPartsNumber());
          // 商品正式名称 = (A).商品正式名称
          stockRankAnalysisResult.setItemName(stockHistoryInfo.getItemName());
          // 売上金額 = (A).在庫履歴情報.売上金額(税抜き) - (A).在庫履歴情報.売上返品金額
          stockRankAnalysisResult
              .setSalesPrice(stockHistoryInfo.getSalesPriceTaxExc().subtract(stockHistoryInfo.getSalesReturnPrice()));
          // 粗利金額 = (A).粗利金額
          stockRankAnalysisResult.setGrossProfit(stockHistoryInfo.getGrossProfit());
          // 出荷数量 = (A).在庫履歴情報.売上数量 - (A).在庫履歴情報.売上返品数
          stockRankAnalysisResult.setShippingQuantity(
              stockHistoryInfo.getSalesQuantity().subtract(stockHistoryInfo.getSalesReturnQuantity()));
          String[] stockInfoMapValue = stockInfoMap.get(keyCompare1).split(Constant.SEPARATER);
          // 棚番 = (B).棚番
          stockRankAnalysisResult.setShelfNum(stockInfoMapValue[0]);
          // 在庫登録日 = (B).登録日時
          stockRankAnalysisResult.setStockCreateDate(new BlCloudDate(stockInfoMapValue[1]));
          // 仕入在庫数 = (B).仕入在庫数
          stockRankAnalysisResult.setPurchaseStockQuantity(new BigDecimal(stockInfoMapValue[2]));
          // 最低在庫数 = (B).最低在庫数
          stockRankAnalysisResult.setMinStockQuantity(new BigDecimal(stockInfoMapValue[3]));
          // 最高在庫数 = (B).最高在庫数
          stockRankAnalysisResult.setMaxStockQuantity(new BigDecimal(stockInfoMapValue[4]));
          // BLコードグループコード = (D).BLコードグループコード
          stockRankAnalysisResult.setBlCdGroupCode(blCodeSetting.getBlCdGroupCode());
          String[] blPrtsCdArray = keyCompare3.split(Constant.SEPARATER);
          String[] blCdGroupCodeSettingValue = blCdGroupCodeSettingMap.get(keyCompare4).split(Constant.SEPARATER);
          stockRankAnalysisResult.setItemMClassCd(Integer.valueOf(blCdGroupCodeSettingValue[0]));
          stockRankAnalysisResult.setItemLClassCd(Integer.valueOf(blCdGroupCodeSettingValue[1]));
          String itemMClassCd = blCdGroupCodeSettingValue[0].toString();
          String[] keyArray = new String[] {
              keyCompare1,
              Constant.SEPARATER + keyCompare2,
              Constant.SEPARATER + keyCompare3,
              Constant.SEPARATER + keyCompare4,
              Constant.COMMA + keyCompare5,
              Constant.SEPARATER + blPrtsCdArray[1],
              Constant.SEPARATER + itemMClassCd };
          for (int i = 0; i < 7; i++) {
            keyOutPutResultFilter = keyStringBuilder.append(keyArray[i]).toString();
          }
        }
        if (Objects.nonNull(keyOutPutResultFilter)) {
          if (!stockRankAnalysisResultMap.containsKey(keyOutPutResultFilter)) {
            stockRankAnalysisResultMap.put(keyOutPutResultFilter, stockRankAnalysisResult);
          }
        }
      });
    });
    return stockRankAnalysisResultMap;
  }

  /**
   * <pre>
   * BLコードグループコード設定 MAP
   * </pre>
   * @param blCdGroupCodeSettingList BLコードグループコード設定
   * @return Map of BLコードグループコード設定
   */
  private Map<String, String> convertBlCdGroupCodeSettingListToMap(List<BlCdGroupCodeSetting> blCdGroupCodeSettingList) {
    Map<String, String> blCdGroupCodeSettingMap = new HashMap<>();
    if (CollectionUtils.isNotEmpty(blCdGroupCodeSettingList)) {
      blCdGroupCodeSettingList.forEach(blCdGroupCodeSetting -> {
        String key = this.getKeyCompare(blCdGroupCodeSetting, Constant.KEY_COMPARE_BL_CD_GROUP_CODE);
        String value = this.getBlCdGroupCodeSettingValue(blCdGroupCodeSetting);
        blCdGroupCodeSettingMap.put(key, value);
      });
    }
    return blCdGroupCodeSettingMap;
  }

  /**
   * <pre>
   * グループコード設定値を取得する
   * </pre>
   * @param entityObject エンティティオブジェクト
   * @return 条件によるキー
   */
  private String getBlCdGroupCodeSettingValue(EntityObject entityObject) {
    StringBuilder value = new StringBuilder();
    String[] valueArr = new String[] {
        String.valueOf(entityObject.getProperty(ITEM_M_CLASS_CD)),
        Constant.SEPARATER + entityObject.getProperty(ITEM_M_CLASS_NAME),
        Constant.SEPARATER + entityObject.getProperty(ITEM_L_CLASS_CD),
        Constant.SEPARATER + entityObject.getProperty(ITEM_L_CLASS_NAME) };
    for (int i = 0; i < 4; i++) {
      value.append(valueArr[i]);
    }
    return value.toString();
  }

  /**
   * <pre>
   * 部品商商品情報 MAP
   * </pre>
   * @param pmItemInfoList 部品商商品情報
   * @return Map 部品商商品情報
   */
  private Map<String, BlTenantId> convertPmItemInfoListToMapByBlCodeSetting(List<PmItemInfo> pmItemInfoList) {

    Map<String, BlTenantId> pmItemInfoMap = new HashMap<>();
    if (CollectionUtils.isNotEmpty(pmItemInfoList)) {
      pmItemInfoList.forEach(pmItemInfo -> {
        String key = this.getKeyCompare(pmItemInfo, Constant.KEY_COMPARE_PM_ITEM_BY_BL_PRTS_CD);
        BlTenantId value = pmItemInfo.getBlTenantId();
        pmItemInfoMap.put(key, value);
      });
    }
    return pmItemInfoMap;
  }

  /**
   * <pre>
   * 在庫情報 MAP
   * </pre>
   * @param stockInfoList 在庫情報
   * @return Map 在庫情報
   */

  private Map<String, String> convertStockInfoListToMap(List<StockInfo> stockInfoList) {
    Map<String, String> stockInfoMap = new HashMap<>();
    stockInfoList.forEach(stockInfo -> {
      String key = this.getKeyCompare(stockInfo, Constant.KEY_COMPARE_STOCK_INFO);
      String value = this.getStockInfoMapValue(stockInfo);
      stockInfoMap.put(key, value);
    });
    return stockInfoMap;
  }

  /**
   * <pre>
   * 在庫情報を見る
   * </pre>
   * @param entityObject エンティティオブジェクト
   * @return 条件によるキー
   */
  private String getStockInfoMapValue(EntityObject entityObject) {
    StringBuilder value = new StringBuilder();
    String[] valueArr = new String[] {
        entityObject.getProperty(SHELF_NUM),
        Constant.SEPARATER + DateTimeUtils
            .toDate(entityObject.getProperty(CREATE_DATE_TIME)),
        Constant.SEPARATER + entityObject.getProperty(PURCHASE_STOCK_QUANTITY),
        Constant.SEPARATER + entityObject.getProperty(MIN_STOCK_QUANTITY),
        Constant.SEPARATER + entityObject.getProperty(MAX_STOCK_QUANTITY),
        Constant.SEPARATER + entityObject.getProperty(STOCK_MGT_DIV_CD_LIST) };
    for (int i = 0; i < valueArr.length; i++) {
      value.append(valueArr[i]);
    }
    return value.toString();
  }

  /**
   * <pre>
   * 仕入先管理設定情報 MAP
   * </pre>
   * @param supplierMgtSettingList 仕入先管理設定情報
   * @return Map  仕入先管理設定
   */
  private Map<String, String> convertSupplierMgtSettingListToMap(List<SupplierMgtSetting> supplierMgtSettingList) {
    Map<String, String> supplierMgtSettingMap = new HashMap<>();
    supplierMgtSettingList.forEach(supplierMgtSetting -> {
      String initKey = this.getKeyCompare(supplierMgtSetting, Constant.KEY_COMPARE_SUPPLIER);
      String key = initKey + Constant.SEPARATER + supplierMgtSetting.getBlPrtsCd() + Constant.SEPARATER
          + supplierMgtSetting.getItemMClassCd();

      String value = this.getSupplierMgtSettingValue(supplierMgtSetting);
      supplierMgtSettingMap.put(key, value);
    });
    return supplierMgtSettingMap;
  }

  /**
   * <pre>
   * 仕入先管理設定情報値を取得
   * </pre>
   * @param supplierMgtSetting  仕入先管理設定情報
   * @return サプライヤ管理設定値
   */
  private String getSupplierMgtSettingValue(SupplierMgtSetting supplierMgtSetting) {

    StringBuilder value = new StringBuilder();
    String[] valueArr = new String[] {
        String.valueOf(supplierMgtSetting.getProperty(SUPPLIER_CD)),
        Constant.SEPARATER + supplierMgtSetting.getProperty(SUPPLIER_ADDR_NAME) };
    for (int i = 0; i < 2; i++) {
      value.append(valueArr[i]);
    }
    return value.toString();
  }

  /**
   * 順位項目の設定
   * @param stockRankQuery 在庫順位分析検索
   * @param resultList 在庫順位分析結果
   */
  private void sortRankAnalysisList(StockRankAnalysisSearch stockRankQuery, List<StockRankAnalysisResult> resultList) {
    Comparator<StockRankAnalysisResult> salePriceRankOrder = null;
    Comparator<StockRankAnalysisResult> grossRankOrder = null;
    Comparator<StockRankAnalysisResult> shipmentRankOrder = null;
    // 売上金額順の設定
    // 在庫順位分析出力結果配列.売上金額 降順
    if (StockRankAnalysisOutputOrderDiv.Low.equals(stockRankQuery.getStockRankAnalysisOutputOrderDiv())) {
      salePriceRankOrder = Comparator.comparing(StockRankAnalysisResult::getSalesPrice);
      grossRankOrder = Comparator.comparing(StockRankAnalysisResult::getGrossProfit);
      shipmentRankOrder = Comparator.comparing(StockRankAnalysisResult::getShippingQuantity);
    } else {
      salePriceRankOrder = Comparator.comparing(StockRankAnalysisResult::getSalesPrice).reversed();
      grossRankOrder = Comparator.comparing(StockRankAnalysisResult::getGrossProfit).reversed();
      shipmentRankOrder = Comparator.comparing(StockRankAnalysisResult::getShippingQuantity).reversed();
    }
    Collections.sort(resultList, salePriceRankOrder);
    int salesPriceRank = 1;
    // 在庫順位分析出力結果配列をloopして、在庫順位分析出力結果配列.売上金額ごとに1からインクリメント
    for (int i = 0; i < resultList.size(); i++) {
      if (salesPriceRank == 1) {
        resultList.get(i).setSalesPriceRank(salesPriceRank++);
      } else if (resultList.get(i).getSalesPrice().equals(resultList.get(i - 1).getSalesPrice())) {
        // 在庫順位分析出力結果配列.売上金額が同額の場合は同順位とする。
        resultList.get(i).setSalesPriceRank(resultList.get(i - 1).getSalesPriceRank());
        salesPriceRank++;
      } else {
        resultList.get(i).setSalesPriceRank(salesPriceRank++);
      }
    }
    // 粗利額順の設定
    // 在庫順位分析出力結果配列.粗利金額 降順
    Collections.sort(resultList, grossRankOrder);
    int grossProfitRank = 1;
    // 在庫順位分析出力結果配列をloopして、在庫順位分析出力結果配列.粗利金額ごとに1からインクリメント
    for (int i = 0; i < resultList.size(); i++) {
      if (grossProfitRank == 1) {
        resultList.get(i).setGrossProfitRank(grossProfitRank++);
      } else if (resultList.get(i).getGrossProfit().equals(resultList.get(i - 1).getGrossProfit())) {
        // 在庫順位分析出力結果配列.粗利金額が同額の場合は同順位とする。
        resultList.get(i).setGrossProfitRank(resultList.get(i - 1).getGrossProfitRank());
        grossProfitRank++;
      } else {
        resultList.get(i).setGrossProfitRank(grossProfitRank++);
      }
    }
    // 出荷数順の設定
    // 在庫順位分析出力結果配列.出荷数量 降順
    Collections.sort(resultList, shipmentRankOrder);
    int shipmentRank = 1;
    // 在庫順位分析出力結果配列をloopして、在庫順位分析出力結果配列.出荷数量ごとに1からインクリメント
    for (int i = 0; i < resultList.size(); i++) {
      if (shipmentRank == 1) {
        resultList.get(i).setShippingQuantityRank(shipmentRank++);
      } else if (resultList.get(i).getShippingQuantity().equals(resultList.get(i - 1).getShippingQuantity())) {
        // 在庫順位分析出力結果配列.出荷数量が同数の場合は同順位とする。
        resultList.get(i).setShippingQuantityRank(resultList.get(i - 1).getShippingQuantityRank());
        shipmentRank++;
      } else {
        resultList.get(i).setShippingQuantityRank(shipmentRank++);
      }
    }
    // 18.出力結果用にソートする。
    // 在庫順位分析検索用情報.在庫順位分析出力順種別が「0:売上金額順」の場合
    // 在庫順位分析結果配列.順位(売上金額)　昇順、在庫順位分析結果配列.順位(粗利額)　昇順、在庫順位分析結果配列.順位(出荷数)　昇順
    if (StockRankAnalysisOutputOrderType.SalesPrice == stockRankQuery.getStockRankAnalysisOutputOrderType()) {
      salePriceRankOrder = Comparator.comparing(StockRankAnalysisResult::getSalesPriceRank);
      grossRankOrder = Comparator.comparing(StockRankAnalysisResult::getGrossProfitRank);
      shipmentRankOrder = Comparator.comparing(StockRankAnalysisResult::getShippingQuantityRank);
      // 順位が全て同一の場合は 仕入先コード＞メーカーコード＞大分類コード＞中分類コード＞グループコード＞品番 の優先度で昇順とする
      Collections.sort(resultList, salePriceRankOrder.thenComparing(grossRankOrder).thenComparing(shipmentRankOrder)
          .thenComparing(Comparator.comparing(StockRankAnalysisResult::getSupplierCd))
          .thenComparing(StockRankAnalysisResult::getItemLClassCd).thenComparing(StockRankAnalysisResult::getItemMClassCd)
          .thenComparing(StockRankAnalysisResult::getBlCdGroupCode)
          .thenComparing(StockRankAnalysisResult::getItemPartsNumber));
    } else if (StockRankAnalysisOutputOrderType.GrossProfit == stockRankQuery.getStockRankAnalysisOutputOrderType()) {
      // 在庫順位分析検索用情報.在庫順位分析出力順種別が「1:粗利額順」の場合
      // 在庫順位分析結果配列.順位(粗利額)　昇順、在庫順位分析結果配列.順位(売上金額)　昇順、在庫順位分析結果配列.順位(出荷数)　昇順
      grossRankOrder = Comparator.comparing(StockRankAnalysisResult::getGrossProfitRank);
      salePriceRankOrder = Comparator.comparing(StockRankAnalysisResult::getSalesPriceRank);
      shipmentRankOrder = Comparator.comparing(StockRankAnalysisResult::getShippingQuantityRank);
      // 順位が全て同一の場合は 仕入先コード＞メーカーコード＞大分類コード＞中分類コード＞グループコード＞品番 の優先度で昇順とする
      Collections.sort(resultList, grossRankOrder.thenComparing(salePriceRankOrder).thenComparing(shipmentRankOrder)
          .thenComparing(Comparator.comparing(StockRankAnalysisResult::getSupplierCd))
          .thenComparing(StockRankAnalysisResult::getItemLClassCd).thenComparing(StockRankAnalysisResult::getItemMClassCd)
          .thenComparing(StockRankAnalysisResult::getBlCdGroupCode)
          .thenComparing(StockRankAnalysisResult::getItemPartsNumber));
    } else if (StockRankAnalysisOutputOrderType.Shipments == stockRankQuery.getStockRankAnalysisOutputOrderType()) {
      // 在庫順位分析検索用情報.在庫順位分析出力順種別が「2:出荷数順」の場合
      // 在庫順位分析結果配列.順位(出荷数)　昇順、在庫順位分析結果配列.順位(売上金額)　昇順、在庫順位分析結果配列.順位(粗利額)　昇順
      shipmentRankOrder = Comparator.comparing(StockRankAnalysisResult::getShippingQuantityRank);
      salePriceRankOrder = Comparator.comparing(StockRankAnalysisResult::getSalesPriceRank);
      grossRankOrder = Comparator.comparing(StockRankAnalysisResult::getGrossProfitRank);
      // 順位が全て同一の場合は 仕入先コード＞メーカーコード＞大分類コード＞中分類コード＞グループコード＞品番 の優先度で昇順とする
      Collections.sort(resultList, shipmentRankOrder.thenComparing(salePriceRankOrder).thenComparing(grossRankOrder)
          .thenComparing(Comparator.comparing(StockRankAnalysisResult::getSupplierCd))
          .thenComparing(StockRankAnalysisResult::getItemLClassCd).thenComparing(StockRankAnalysisResult::getItemMClassCd)
          .thenComparing(StockRankAnalysisResult::getBlCdGroupCode)
          .thenComparing(StockRankAnalysisResult::getItemPartsNumber));
    }
  }

  /**
   * <pre>
   * 在庫履歴情報リストから、指定されたPropertyDefineに合致する項目データを取得し、
   * 重複を排除して在庫履歴情報リストを再作成する。
   * </pre>
   * @param stockHistoryInfoList 在庫履歴情報リスト
   * @param gettingNames 取得対象のPropertyDefine
   * @return undupliStockHistoryInfoList 在庫履歴情報(重複無し)
   */
  private List<StockHistoryInfo> getUnduplicatedStockHistoryInfo(List<StockHistoryInfo> stockHistoryInfoList,
                                                                 List<PropertyDefine<?>> gettingNames) {
    // 値が重複しているかを確認するマップ。
    Map<String, StockHistoryInfo> undupliMakerCdPartsNumMap = new HashMap<String, StockHistoryInfo>();
    // 重複を除いた在庫履歴情報リスト
    List<StockHistoryInfo> undupliStockHistoryInfoList = new ArrayList<StockHistoryInfo>();
    String key = "";
    for (StockHistoryInfo stockHistoryInfo : stockHistoryInfoList) {
      StringBuffer keyString = new StringBuffer();
      for (PropertyDefine<?> gettingName : gettingNames) {
        // PropertyDefineをキーに在庫履歴情報から指定項目の値を取得する
        keyString.append(stockHistoryInfo.getProperty(gettingName).toString());
      }
      key = keyString.toString();
      // キーが存在しない場合は重複なし、Mapに在庫履歴情報を追加する。
      if (!undupliMakerCdPartsNumMap.containsKey(key)) {
        undupliMakerCdPartsNumMap.put(key, stockHistoryInfo);
      }
    }
    // Mapに値が入っている場合はMap→在庫履歴情報リストに変換する。
    if (undupliMakerCdPartsNumMap.size() > 0) {
      undupliStockHistoryInfoList = new ArrayList<StockHistoryInfo>(undupliMakerCdPartsNumMap.values());
    }
    // 在庫履歴情報リストを返却する
    return undupliStockHistoryInfoList;
  }

  /**
   * <pre>
   * 部品商商品情報 MAP
   * </pre>
   * @param pmItemInfoList  部品商商品情報
   * @return Map 部品商商品情報
   */
  private Map<String, Integer> convertPmItemInfoListToMapBySI(List<PmItemInfo> pmItemInfoList) {
    Map<String, Integer> pmItemInfoMap = new HashMap<>();
    if (CollectionUtils.isNotEmpty(pmItemInfoList)) {
      pmItemInfoList.forEach(pmItemInfo -> {
        String key = this.getKeyCompare(pmItemInfo, Constant.KEY_COMPARE_PM_ITEM_BY_STOCK);
        Integer value = pmItemInfo.getBlPrtsCd();
        pmItemInfoMap.put(key, value);
      });
    }
    return pmItemInfoMap;
  }

  /**
   * <pre>
   * キー比較を取得
   * </pre>
   * @param entityObject エンティティオブジェクト
   * @param keyCompare キー比較
   * @return サプライヤ管理設定値
   */
  private String getKeyCompare(EntityObject entityObject, int keyCompare) {

    StringBuilder key = new StringBuilder();
    String[] keyArr = null;
    String organizationCodeProp = null;
    if (entityObject instanceof StockHistoryInfo) {
      organizationCodeProp = entityObject.getProperty(ORGANIZATION_CODE);
    } else {
      organizationCodeProp = entityObject.getProperty(WH_ORGANIZATION_CODE);
    }
    switch (keyCompare) {
    case Constant.KEY_COMPARE_STOCK_INFO:
      // 在庫履歴情報
      keyArr = new String[] {
          String.valueOf(entityObject.getProperty(BL_TENANT_ID)),
          Constant.SEPARATER + organizationCodeProp,
          Constant.SEPARATER + entityObject.getProperty(WH_CODE),
          Constant.SEPARATER + entityObject.getProperty(DISPLAY_ITEM_MAKER_CD),
          Constant.SEPARATER + entityObject.getProperty(ITEM_PARTS_NUMBER) };
      for (int i = 0; i < 5; i++) {
        key.append(keyArr[i]);
      }
      return key.toString();
    case Constant.KEY_COMPARE_PM_ITEM_BY_STOCK:
      // 在庫履歴情報
      keyArr = new String[] {
          entityObject.getProperty(DISPLAY_ITEM_MAKER_CD).toString(),
          Constant.SEPARATER + entityObject.getProperty(ITEM_PARTS_NUMBER) };
      for (int i = 0; i < 2; i++) {
        key.append(keyArr[i]);
      }
      return key.toString();
    case Constant.KEY_COMPARE_PM_ITEM_BY_BL_PRTS_CD:
      keyArr = new String[] {
          String.valueOf(entityObject.getProperty(BL_TENANT_ID)),
          Constant.SEPARATER + entityObject.getProperty(BL_PRTS_CD) };
      for (int i = 0; i < 2; i++) {
        key.append(keyArr[i]);
      }
      return key.toString();
    case Constant.KEY_COMPARE_BL_CD_GROUP_CODE:
      keyArr = new String[] {
          String.valueOf(entityObject.getProperty(BL_TENANT_ID)),
          Constant.SEPARATER + entityObject.getProperty(BL_CD_GROUP_CODE) };
      for (int i = 0; i < 2; i++) {
        key.append(keyArr[i]);
      }
      return key.toString();
    default:
      keyArr = new String[] {
          entityObject.getProperty(ORGANIZATION_CODE),
          Constant.SEPARATER + entityObject.getProperty(ITEM_PARTS_NUMBER),
          Constant.SEPARATER + entityObject.getProperty(ITEM_MAKER_CD) };
      for (int i = 0; i < 3; i++) {
        key.append(keyArr[i]);
      }
      return key.toString();
    }
  }

  /**
   * <pre>
   * 仕入先管理情報取得
   * </pre>
   * @param stockHistoryInfoList 在庫履歴情報 (A)
   * @param pmItemInfoList 部品商商品情報 (C)
   * @param blCdGroupCodeSettingList BLコードグループコード設定情報 (E)
   * @param blCodeSettingMap BLコード設定情報 (D)
   * @return SupplierMgtSetting 仕入先管理設定情報
   */
  private List<SupplierMgtSetting> searchSupplierMgtSetting(List<StockHistoryInfo> stockHistoryInfoList,
                                                            List<PmItemInfo> pmItemInfoList,
                                                            List<BlCdGroupCodeSetting> blCdGroupCodeSettingList,
                                                            Map<Integer, String> blCodeSettingMap) {
    // 仕入先管理情報リスト
    List<SupplierMgtSetting> supplierMgtSettingList = new ArrayList<>();

    // 在庫履歴情報の重複排除の条件項目リスト
    List<PropertyDefine<?>> gettingNames = new ArrayList<PropertyDefine<?>>();
    gettingNames.add(BL_TENANT_ID); // BLテナントID
    gettingNames.add(ORGANIZATION_CODE); // 組織コード
    gettingNames.add(DISPLAY_ITEM_MAKER_CD); // 表示用商品メーカーコード
    gettingNames.add(ITEM_MAKER_CD); // 商品メーカーコード
    gettingNames.add(ITEM_PARTS_NUMBER); // 商品品番
    // 重複を排除した在庫履歴情報を取得
    List<StockHistoryInfo> undupliStockHistoryInfoList = this.getUnduplicatedStockHistoryInfo(stockHistoryInfoList,
        gettingNames);
    // 部品商商品情報を取得する
    Map<String, Integer> pmItemInfoMap = this.convertPmItemInfoListToMapBySI(pmItemInfoList);

    // 11.仕入先管理設定_優先仕入先検索のINPUT情報用意
    // 選択条件に値を設定する
    if (!CollectionUtils.isEmpty(undupliStockHistoryInfoList)) {
      undupliStockHistoryInfoList.forEach(stockHistoryInfo -> {
        SupplierMgtSetting supplierMgtSetting = new SupplierMgtSetting();
        // BLテナントID (J).BLテナントID
        supplierMgtSetting.setBlTenantId(stockHistoryInfo.getBlTenantId());
        // 組織コード (J).組織コード
        supplierMgtSetting.setOrganizationCode(stockHistoryInfo.getOrganizationCode());
        // 商品品番 (J).商品品番
        supplierMgtSetting.setItemPartsNumber(stockHistoryInfo.getItemPartsNumber());
        // 商品メーカーコード(J).商品メーカーコード
        supplierMgtSetting.setItemMakerCd(stockHistoryInfo.getItemMakerCd());
        // BL部品コード (C).BL部品コード
        if (!pmItemInfoMap.isEmpty()) {
          String key = stockHistoryInfo.getDisplayItemMakerCd() + Constant.SEPARATER + stockHistoryInfo.getItemPartsNumber();
          if (pmItemInfoMap.containsKey(key)) {
            supplierMgtSetting.setBlPrtsCd(pmItemInfoMap.get(key));
          }
        }
        // 商品中分類コード.商品中分類コード
        if (!BL_PRTS_CD.getDefaultValue().equals(supplierMgtSetting.getBlPrtsCd())) {
          // (C).BL部品コードをキーに(D).BLコードグループコードを取得する
          if (!blCodeSettingMap.isEmpty() && !blCdGroupCodeSettingList.isEmpty()) {
            String blPrtsCd = blCodeSettingMap.get(supplierMgtSetting.getBlPrtsCd());
            String[] blCdGroupCode = blPrtsCd != null ? blPrtsCd.split(Constant.SEPARATER) : null;
            Map<Integer, Integer> blCdGroupCodeSettingMap = blCdGroupCodeSettingList.stream().collect(
                Collectors.toMap(BlCdGroupCodeSetting::getBlCdGroupCode, BlCdGroupCodeSetting::getItemMClassCd));
            // (D).BLコードグループコードをキーに(E).商品中分類コードを取得する
            if (blCdGroupCode != null && blCdGroupCodeSettingMap.containsKey(Integer.parseInt(blCdGroupCode[0]))) {
              supplierMgtSetting.setItemMClassCd(blCdGroupCodeSettingMap.get(Integer.parseInt(blCdGroupCode[0])));
            }
          }
        }
        supplierMgtSettingList.add(supplierMgtSetting);
      });
    }
    List<SupplierMgtSetting> listSupplier = new ArrayList<SupplierMgtSetting>();
    if (CollectionUtils.isNotEmpty(supplierMgtSettingList)) {
      listSupplier = stockLogic.getListSupplierMgtSetting(supplierMgtSettingList);
    }
    return listSupplier;
  }

  /**
   * <pre>
   * BLコード設定の取得結果より、
   * BL部品コード をキー、BLコードグループコード＋":"＋BLコードグループ全角名称を値としたマップに格納する。
   * </pre>
   * @param blCodeSettingList BLコード設定List
   * @return blCodeSettingMap BLコード設定Map
   */
  private Map<Integer, String> convertBlCodeSettingListToMap(List<BlCodeSetting> blCodeSettingList) {
    Map<Integer, String> blCodeSettingMap = new HashMap<Integer, String>();
    if (!blCodeSettingList.isEmpty()) {
      blCodeSettingList.forEach(blCodeSetting -> {
        String value = blCodeSetting.getBlCdGroupCode() + Constant.SEPARATER + blCodeSetting.getBlCdGroupFullWName();
        blCodeSettingMap.put(blCodeSetting.getBlPrtsCd(), value);
      });
    }
    return blCodeSettingMap;
  }

  /**
   * CSVファイルのダウンロード
   * @param subject URLパス経由で取得
   * @param id ダウンロードするファイルID
   * @return ファイルダウンロード情報を含むResponseEntity
   */
  @RequestMapping(value = { "/{subject:[a-zA-Z\\-_]+}/{id}/csv" }, method = { RequestMethod.GET })
  public final ResponseEntity<?> downloadFile(@PathVariable String subject, @PathVariable String id) {
    this.logger.info("{}", id);
    return commonService.downloadFile(subject, id, this.getBlTenantId());
  }

  /**
   * プロパティ形式リストを作成します。
   * @return プロパティ形式リスト
   */
  private List<PropertyDefineNeedFormat> createListProNeedFormat() {
    List<PropertyDefineNeedFormat> listPro = new ArrayList<>();
    listPro.add(new PropertyDefineNeedFormat(SUPPLIER_CD, 9));
    listPro.add(new PropertyDefineNeedFormat(STOCK_CREATE_DATE, 0, Constant.FORMAT_DATE_FOR_REPORT));
    return listPro;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public StockRankAnalysisSearch newEntity() {
    return new StockRankAnalysisSearch();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getResourceName() {
    return RESOURCE_NAME;
  }
}
