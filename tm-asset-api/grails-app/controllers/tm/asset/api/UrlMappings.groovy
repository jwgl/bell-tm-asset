package tm.asset.api

class UrlMappings {

    static mappings = {
        "/users"(resources: 'user', includes:[]){
            "/places"(resources: 'place')
            "/receiptForms"(resources: 'receiptForm') {
                "/approvers"(controller: 'receiptForm', action: 'approvers', method: 'GET')
            }
            "/centers"(resources: 'assetCenter')
            "/areas"(resources: 'area')
            "/transferForms"(resources: 'transferForm') {
                "/approvers"(controller: 'transferForm', action: 'approvers', method: 'GET')
            }
        }
        "/approvers"(resources: 'approver', includes:[]){
            "/receiptApprovals"(resources: 'receiptApproval', includes:['index']) {
                "/workitems"(resources: 'receiptApproval', includes: ['show', 'patch'])
            }
            "/checkoutApprovals"(resources: 'checkoutApproval', includes:['index']) {
                "/workitems"(resources: 'checkoutApproval', includes: ['show', 'patch'])
            }
        }

        "/models"(resources: 'model', include: ['index'])

        "500"(view: '/error')
        "404"(view: '/notFound')
    }
}
