package cn.edu.bnuz.bell.asset

import cn.edu.bnuz.bell.report.ReportClientService
import cn.edu.bnuz.bell.report.ReportRequest
import cn.edu.bnuz.bell.security.SecurityService
import org.springframework.security.access.prepost.PreAuthorize

@PreAuthorize('hasAuthority("PERM_ASSET_VIEW")')
class OutputController {
    ReportClientService reportClientService
    SecurityService securityService

    def index(DownloadCommand cmd) {
        def reportName = "asset-${cmd.type}"
        def format = 'xlsx'
        def parameters = [admin: securityService.hasRole('ROLE_ASSET_CENTER_ADMIN'), userId: securityService.userId]
        def reportRequest = new ReportRequest(
                reportName: reportName,
                format: format,
                parameters: parameters
        )
        reportClientService.runAndRender(reportRequest, response)
    }
}
