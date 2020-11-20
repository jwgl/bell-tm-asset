package cn.edu.bnuz.bell.asset

import cn.edu.bnuz.bell.security.SecurityService
import cn.edu.bnuz.bell.security.User
import grails.gorm.transactions.Transactional

@Transactional
class LogService {
    SecurityService securityService

    def list() {
        Log.executeQuery'''
select new map(
    l.id as id,
    l.dateCreated as dateCreated,
    u.name as userName,
    l.action as action,
    l.note as note,
    l.ip as ip,
    r.name as place,
    r.building as building,
    a.name as assetName
)
from Log l
join l.user u
left join l.room r
left join l.asset a
order by l.id desc'''
    }

    def log(String action, String note, Room room, Asset asset) {
        Log log = new Log(
                user: User.load(securityService.userId),
                ip: securityService.ipAddress,
                dateCreated: new Date(),
                action: action,
                note: note,
                room: room,
                asset: asset,
        )
        log.save()
    }
}
