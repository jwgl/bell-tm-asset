package cn.edu.bnuz.bell.asset

import org.springframework.security.access.prepost.PreAuthorize


@PreAuthorize('hasRole("ROLE_ASSET_BUILDING_ADMIN")')
class AssetCartController {
    AssetCartService assetCartService
	
    def index(String userId) {
        renderJson([list: assetCartService.list(userId)])
    }

    def save(String userId) {
        CartCommand cartCommand = new CartCommand()
        bindData(cartCommand, request.JSON)
        assetCartService.create(userId, cartCommand)
        renderJson([id: cartCommand.cartName])
    }

    def delete(String userId, Long id) {
        assetCartService.delete(userId)
        renderOk()
    }
}
