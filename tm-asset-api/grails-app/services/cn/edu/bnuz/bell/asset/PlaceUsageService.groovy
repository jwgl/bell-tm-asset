package cn.edu.bnuz.bell.asset

import cn.edu.bnuz.bell.asset.eto.PlaceUsageEto
import cn.edu.bnuz.bell.master.Term
import cn.edu.bnuz.bell.master.TermService
import grails.gorm.transactions.Transactional

import java.time.temporal.ChronoUnit

@Transactional
class PlaceUsageService {
    TermService termService
    def getFormForCreate() {
        Term term = termService.activeTerm
        return [startDate: term.startDate, form: [:]]
    }

    def getContact(PlaceUsageCommand cmd) {
        Term term = termService.activeTerm
        def week = (term.startDate.until(cmd.date, ChronoUnit.DAYS) / 7 +1) as Integer
        def dayOfWeek = cmd.date.dayOfWeek.value
        def odd = week % 2 == 1 ? '单' : '双'
        PlaceUsageEto.executeQuery'''
select new map(
contact as contact,
department as department
)
from PlaceUsageEto
where termId = :termId
and :week between weekStart and weekEnd
and dayOfWeek = :dayOfWeek
and (evenOdd is null or evenOdd = :odd)
and :section between section and section + sectionCount - 1 end
''',[termId: term.id, week: week, dayOfWeek: dayOfWeek, odd: odd, section: cmd.section]
    }
}
