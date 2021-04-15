package cn.edu.bnuz.bell.asset

import org.springframework.security.access.prepost.PreAuthorize


@PreAuthorize('hasRole("ROLE_ASSET_BUILDING_ADMIN")')
class CartHistoryController {
    AssetCartService assetCartService

    def index(String userId) {
        renderJson(assetCartService.history(userId))
    }

    def show(String userId, String id) {
        renderJson(assetCartService.getCartItems(userId, id))
    }
}
