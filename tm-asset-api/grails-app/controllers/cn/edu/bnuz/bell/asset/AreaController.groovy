package cn.edu.bnuz.bell.asset

import org.springframework.security.access.prepost.PreAuthorize


@PreAuthorize('hasRole("ROLE_ASSET_BUILDING_ADMIN")')
class AreaController {

    AreaService areaService


    def index(AssetOptionCommand cmd) {
        renderJson areaService.list(cmd)
    }

    def show(Long id) {
        renderJson areaService.getFormInfo(id)
    }
}
