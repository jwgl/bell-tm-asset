package cn.edu.bnuz.bell.asset

import org.springframework.security.access.prepost.PreAuthorize


@PreAuthorize('hasAuthority("PERM_ASSET_PLACE_PUBLIC")')
class PlacePublicController {
    PlaceService placeService
    def index(RoomOptionCommand cmd) {
        renderJson([rooms: placeService.list(cmd)])
    }

    def show(Long id) {
        renderJson([form: placeService.getFormInfo(id)])
    }
}
