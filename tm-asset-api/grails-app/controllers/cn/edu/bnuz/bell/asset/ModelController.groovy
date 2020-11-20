package cn.edu.bnuz.bell.asset


class ModelController {
	ReceiptFormService receiptFormService
    def index(String q) {
        renderJson receiptFormService.findModels(q)
    }
}
