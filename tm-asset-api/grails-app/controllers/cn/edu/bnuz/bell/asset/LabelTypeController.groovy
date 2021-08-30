package cn.edu.bnuz.bell.asset

import org.springframework.security.access.prepost.PreAuthorize


@PreAuthorize('hasAuthority("PERM_ASSET_LABEL_ADMIN")')
class LabelTypeController {

    def index() { }
}
