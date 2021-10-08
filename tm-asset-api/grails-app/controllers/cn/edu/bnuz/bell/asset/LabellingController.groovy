package cn.edu.bnuz.bell.asset

import org.springframework.security.access.prepost.PreAuthorize


@PreAuthorize('hasAuthority("PERM_ASSET_LABEL_ADMIN")')
class LabellingController {
    LabelService labelService

    def index() { }

    def save() {
        RoomLabelCommand cmd = new RoomLabelCommand()
        bindData(cmd, request.JSON)
        def form = labelService.createRoomLabel(cmd)
        renderJson([id: form.id])
    }
}
