package cn.edu.bnuz.bell.asset

import org.springframework.security.access.prepost.PreAuthorize


class ModelController {
	AssetModelService assetModelService
    def index(String q) {
        if (q) {
            renderJson assetModelService.list(q)
        } else {
            renderJson assetModelService.list()
        }
    }

    @PreAuthorize('hasAuthority("PERM_ASSET_ADVICE_WRITE")')
    def save() {
        AssetModelCommand cmd = new AssetModelCommand()
        bindData(cmd, request.JSON)
        def form = assetModelService.create(cmd)
        renderJson([id: form.id])
    }

}
