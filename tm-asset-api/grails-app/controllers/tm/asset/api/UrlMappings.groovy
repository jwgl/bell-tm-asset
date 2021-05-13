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
                collection {
                    "/upload"(controller: 'transferForm', action: 'upload', method: 'POST')
                }
            }
            "/carts"(resources: 'assetCart')
            "/cartHistory"(resources: 'cartHistory')
            "/scraps"(resources: 'scrap') {
                "/approvers"(controller: 'scrap', action: 'approvers', method: 'GET')
            }
        }
        "/approvers"(resources: 'approver', includes:[]){
            "/receiptApprovals"(resources: 'receiptApproval', includes:['index']) {
                "/workitems"(resources: 'receiptApproval', includes: ['show', 'patch'])
            }
            "/transferApprovals"(resources: 'transferApproval', includes:['index']) {
                "/workitems"(resources: 'transferApproval', includes: ['show', 'patch'])
            }
            "/scrapApprovals"(resources: 'scrapApproval', includes:['index']) {
                "/workitems"(resources: 'scrapApproval', includes: ['show', 'patch'])
            }
            "/userAreas"(resources: 'userArea')
        }

        "/models"(resources: 'model', include: ['index', 'save'])
        "/output"(resources: 'output', include: ['index'])

        "500"(view: '/error')
        "404"(view: '/notFound')
    }
}
