package cn.edu.bnuz.bell.asset

import org.springframework.security.access.prepost.PreAuthorize


@PreAuthorize('hasRole("ROLE_ASSET_BUILDING_ADMIN")')
class AreaController {

    AssetCenterService assetCenterService

    def index(AssetOptionCommand cmd) {
        renderJson assetCenterService.list(cmd)
    }
}
