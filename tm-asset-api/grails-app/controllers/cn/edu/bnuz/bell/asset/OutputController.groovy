package cn.edu.bnuz.bell.asset

import cn.edu.bnuz.bell.http.BadRequestException
import cn.edu.bnuz.bell.report.ReportClientService
import cn.edu.bnuz.bell.report.ReportRequest
import cn.edu.bnuz.bell.security.SecurityService
import org.springframework.security.access.prepost.PreAuthorize

@PreAuthorize('hasAuthority("PERM_ASSET_VIEW")')
class OutputController {
    ReportClientService reportClientService
    SecurityService securityService
    AssetCartService assetCartService

    def index(DownloadCommand cmd) {
        def reportName = "asset-${cmd.type}"
        def format = 'xlsx'
        def parameters
        switch (cmd.type) {
            case 'device':
                def ids = ""
                if (cmd.cartName) {
                    def items = assetCartService.getCartItems(securityService.userId, cmd.cartName)
                    if (items.size() > 0) {
                        items.each {
                            ids += it.id + '-'
                        }
                        ids = "${ids.substring(0, ids.length() - 1)}"
                    }
                } else if (cmd.ids) {
                    ids = cmd.ids
                }
                parameters = [
                        admin: securityService.hasRole('ROLE_ASSET_CENTER_ADMIN'),
                        userId: securityService.userId,
                        ids: ids == '' ? null : ids
                ]
                break
            case 'device-all':
                parameters = [
                        admin: securityService.hasRole('ROLE_ASSET_CENTER_ADMIN')
                ]
                break
            case 'room':
                parameters = [
                        admin: securityService.hasRole('ROLE_ASSET_CENTER_ADMIN'),
                        userId: securityService.userId
                ]
                break
            default: throw new BadRequestException()
        }

        def reportRequest = new ReportRequest(
                reportName: reportName,
                format: format,
                parameters: parameters
        )
        reportClientService.runAndRender(reportRequest, response)
    }
}
