package tm.asset.api

class UrlMappings {

    static mappings = {
        "/users"(resources: 'user', includes:[]){
            "/places"(resources: 'place') {
                "/pictures"(controller: 'place', action: 'savePicture', method: 'POST')
            }
            "/placePublics"(resources: 'placePublic')
            "/receiptForms"(resources: 'receiptForm') {
                "/approvers"(controller: 'receiptForm', action: 'approvers', method: 'GET')
            }
            "/centers"(resources: 'assetCenter')
            "/areas"(resources: 'area')
            "/transferForms"(resources: 'transferForm') {
                "/approvers"(controller: 'transferForm', action: 'approvers', method: 'GET')
            }
            "/carts"(resources: 'assetCart')
            "/cartHistory"(resources: 'cartHistory')
            "/scraps"(resources: 'scrap') {
                "/approvers"(controller: 'scrap', action: 'approvers', method: 'GET')
                collection {
                    "/upload"(controller: 'scrap', action: 'upload', method: 'POST')
                }
            }
            "/labelAdmins"(resources: 'labelAdmin')
            "/labellings"(resources: 'labelling')
            "/plans"(resources: 'plan')
            "/hindFields"(resources: 'hindField')
            "/serviceLogs"(resources: 'placeServiceLog') {
                collection {
                    "/places"(controller: 'placeServiceLog', action: 'place', method: 'GET')
                    "/contacts"(controller: 'placeServiceLog', action: 'contact', method: 'GET')
                    "/assets"(controller: 'placeServiceLog', action: 'asset', method: 'GET')
                }
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
        "/messages"(resources: 'messager')
        "/placePictures"(resources: 'placePicture')
        "/models"(resources: 'model', include: ['index', 'save'])
        "/output"(resources: 'output', include: ['index'])
        "/attachments"(resources: 'attachment', includes: ['index'])

        "500"(view: '/error')
        "404"(view: '/notFound')
    }
}
