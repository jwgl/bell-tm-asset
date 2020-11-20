package cn.edu.bnuz.bell.asset

import cn.edu.bnuz.bell.security.SecurityService
import org.springframework.security.access.prepost.PreAuthorize


@PreAuthorize('hasAuthority("PERM_ASSET_VIEW")')
class PlaceController {

    PlaceService placeService
    SecurityService securityService

    def index(RoomOptionCommand cmd) {
        renderJson([createAble: securityService.hasPermission('PERM_ASSET_PLACE_WRITE'), rooms: placeService.list(cmd)])
    }

    def create(){
        renderJson placeService.formForCreate
    }

    @PreAuthorize('hasAuthority("PERM_ASSET_PLACE_WRITE")')
    def save() {
        RoomCommand cmd = new RoomCommand()
        bindData(cmd, request.JSON)
        def form = placeService.create(cmd)
        renderJson([id: form.id])
    }

    def show(Long id) {
        renderJson placeService.getFormInfo(id)
    }

    @PreAuthorize('hasAuthority("PERM_ASSET_PLACE_WRITE")')
    def edit(Long id) {
        renderJson placeService.getFormForEdit(id)
    }
}
