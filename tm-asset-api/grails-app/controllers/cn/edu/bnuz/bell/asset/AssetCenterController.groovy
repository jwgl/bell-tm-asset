package cn.edu.bnuz.bell.asset

import cn.edu.bnuz.bell.http.BadRequestException
import org.springframework.security.access.prepost.PreAuthorize


@PreAuthorize('hasRole("ROLE_ASSET_CENTER_ADMIN")')
class AssetCenterController {
    AssetCenterService assetCenterService
    ReceiptFormService receiptFormService
    AreaService areaService
    AssetModelService assetModelService

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

    def show(Long id) {
        def formInfoForUpdate = areaService.getFormInfo(id)
        if (!formInfoForUpdate) {
            throw new BadRequestException()
        }
        formInfoForUpdate['assetModels'] = assetModelService.list()
        formInfoForUpdate['supplies'] = receiptFormService.supplies
        renderJson(formInfoForUpdate)
    }
}
