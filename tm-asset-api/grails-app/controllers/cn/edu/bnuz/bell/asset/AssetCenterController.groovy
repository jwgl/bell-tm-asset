package cn.edu.bnuz.bell.asset

import org.springframework.security.access.prepost.PreAuthorize


@PreAuthorize('hasRole("ROLE_ASSET_CENTER_ADMIN")')
class AssetCenterController {
    AssetCenterService assetCenterService

    def index(AssetOptionCommand cmd) {
        renderJson assetCenterService.list(cmd)
    }

    /**
     * 批量修改编号
     */
    def save() {
        String data = request.JSON['data']
        renderJson assetCenterService.update(data)
    }
}
