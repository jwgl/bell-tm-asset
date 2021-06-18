package cn.edu.bnuz.bell.asset

class TransferFormCommand {
    Long id
    String note
    Long fromId
    String transferType
    String fileName
    Long toId
    List<TransferFormItem> addedItems
}

class TransferFormItem {
    Long id
    String note
}