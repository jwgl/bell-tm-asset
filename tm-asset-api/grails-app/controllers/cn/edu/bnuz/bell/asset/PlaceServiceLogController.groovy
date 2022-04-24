package cn.edu.bnuz.bell.asset

import org.springframework.security.access.prepost.PreAuthorize


@PreAuthorize('hasAuthority("PERM_ASSET_PLACE_SERVICE")')
class PlaceServiceLogController {
    PlaceServiceLogService placeServiceLogService
    HindFieldService hindFieldService
	
    def index() {
        renderJson ([logs: placeServiceLogService.list(),
                     fields: hindFieldService.findByTableName("serviceLog")])
    }

    def create(){
        renderJson placeServiceLogService.formForCreate
    }

    def save() {
        ServiceLogCommand cmd = new ServiceLogCommand()
        bindData(cmd, request.JSON)
        def form = placeServiceLogService.create(cmd)
        renderJson([id: form.id])
    }

    def place(String q) {
        renderJson placeServiceLogService.findPlace(q)
    }

    def contact(PlaceUsageCommand cmd) {
        renderJson placeServiceLogService.findContact(cmd)
    }

    def asset(PlaceUsageCommand cmd) {
        renderJson placeServiceLogService.findAsset(cmd)
    }
}
