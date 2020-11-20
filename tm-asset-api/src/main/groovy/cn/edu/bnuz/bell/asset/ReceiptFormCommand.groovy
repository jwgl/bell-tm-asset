package cn.edu.bnuz.bell.asset

class ReceiptFormCommand {
    Long id
    String note
    List<Item> addedItems
    List<Item> removedItems
}

class Item {
    Long id
    String sn
    String code
    String name
    BigDecimal price
    String dateBought
    Integer qualifyMonth
    Long supplierId
    Long assetModelId
    String assetType
    String unit
    Integer pcs
    String note
}
