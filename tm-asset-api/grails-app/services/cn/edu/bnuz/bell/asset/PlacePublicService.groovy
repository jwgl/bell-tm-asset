package cn.edu.bnuz.bell.asset

import cn.edu.bnuz.bell.master.Term
import grails.gorm.transactions.Transactional
import cn.edu.bnuz.bell.asset.dv.DvRoom

@Transactional
class PlacePublicService {
    def list(RoomPublicOptionCommand cmd) {
        def sqlStr = '''
select new map(
dr.id as id,
dr.name as name,
dr.building as building,
dr.seat as seat,
dr.measure as measure,
dr.status as status,
dr.department as department,
dr.groups as groups,
dr.roomType as roomType,
dr.planning as planning,
dr.labels as labels
)
from DvRoom dr
where dr.id > 100 and dr.status <> 'DELETED'
'''
        if (!cmd.criterion.isEmpty()) {
            sqlStr += " ${cmd.criterion} order by dr.building, dr.name"
        } else {
            return null
        }
        DvRoom.executeQuery sqlStr, cmd.args
    }

    def getTerms() {
        Term.executeQuery("select t.id from Term t where t.active is true or exists (select 1 from Term where t.id = id + 1 and active is true)")
    }
}
