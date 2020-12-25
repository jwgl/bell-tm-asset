package cn.edu.bnuz.bell.asset

import org.springframework.security.access.prepost.PreAuthorize


@PreAuthorize('hasRole("ROLE_ASSET_DIRECTOR")')
class UserAreaController {
    UserAreaService userAreaService
    def index() {
        renderJson userAreaService.list()
    }

    def create() {
        renderJson userAreaService.formForCreate
    }

    def save() {
        UserAreaCommand cmd = new UserAreaCommand()
        bindData(cmd, request.JSON)
        userAreaService.create(cmd)
        renderJson([id: 1])
    }

    def delete(Long id) {
        userAreaService.delete(id)
        renderOk()
    }
}
