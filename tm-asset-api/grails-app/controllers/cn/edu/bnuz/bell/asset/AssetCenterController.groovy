package cn.edu.bnuz.bell.asset

import cn.edu.bnuz.bell.http.BadRequestException
import org.springframework.security.access.prepost.PreAuthorize


@PreAuthorize('hasRole("ROLE_ASSET_CENTER_ADMIN")')
class AssetCenterController {
    AssetCenterService assetCenterService
    ReceiptFormService receiptFormService
    AreaService areaService

    def index(AssetOptionCommand cmd) {
        renderJson assetCenterService.list(cmd)
    }

    /**
     * 批量修改编号
     */
    def save() {
        String data = request.JSON['data']
        renderJson assetCenterService.batchUpdate(data)
    }

    def show(Long id) {
        def formInfoForUpdate = areaService.getFormInfo(id)
        if (!formInfoForUpdate) {
            throw new BadRequestException()
        }
        formInfoForUpdate['suppliers'] = receiptFormService.supplies
        renderJson(formInfoForUpdate)
    }

    def update(Long id) {
        AssetCommand cmd = new AssetCommand()
        bindData(cmd, request.JSON)
        cmd.id = id
        assetCenterService.update(cmd)
        renderOk()
    }
}
