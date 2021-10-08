package cn.edu.bnuz.bell.asset

import org.springframework.security.access.prepost.PreAuthorize

@PreAuthorize('hasAuthority("PERM_ASSET_PLACE_WRITE")')
class RoomLifeController {
    RoomLifeService roomLifeService
    def index() { }


    def edit(Long id) {
        renderJson roomLifeService.getFormForEdit(id)
    }

    def update(Long id) {
        RoomLifeCommand cmd = new RoomLifeCommand()
        bindData(cmd, request.JSON)
        roomLifeService.update(cmd)
        renderOk()
    }
}
