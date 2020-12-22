package cn.edu.bnuz.bell.asset

import cn.edu.bnuz.bell.organization.Teacher
import cn.edu.bnuz.bell.security.SecurityService
import cn.edu.bnuz.bell.security.User
import grails.gorm.transactions.Transactional

@Transactional
class AreaService {
    SecurityService securityService

    def list(AssetOptionCommand cmd) {
        def sqlStr = '''
select new map(
    a.id as id,
    a.sn as sn,
    a.code as code,
    a.name as name,
    a.unit as unit,
    a.dateBought as dateBought,
    a.qualifyMonth as qualifyMonth,
    a.assetType as assetType,
    a.pcs as pcs,
    a.note as note,
    a.state as state,
    s.name as supplier,
    r.building as building,
    r.id as roomId,
    r.name as place,
    m.id as assetModelId,
    m.brand as brand,
    m.specs as specs,
    m.parameter as parameter
)
from Asset a
left join a.assetModel m
left join a.room r
left join a.supplier s
where 
(r.building in :areas or r.id = 1)
and not exists(select tf.id from TransferItem ti join ti.transferForm tf where tf.status in ('CREATED', 'SUBMITTED') and ti.asset.id = a.id)
'''
        if (!cmd.criterion.isEmpty()) {
            sqlStr += " and ${cmd.criterion}"
        }
        sqlStr += ' order by r.building, r.name'
        def list = Asset.executeQuery sqlStr, cmd.args + [areas: areas]
        return [
                list: list,
                buildings: buildings,
                places: places,
                assetNames: assetNames,
                states: states,
                areas: areas,
        ]
    }

    def getAssetNames() {
        Asset.executeQuery'''
select distinct new map(a.name as name, a.name as value)
from Asset a
left join a.room r
where r.building in :buildings or r.id = 1
order by a.name''', [buildings: areas]
    }

    def getStates() {
        Asset.executeQuery("select distinct new map(state as name, state as value) from Asset order by state")
    }

    def getBuildings() {
        UserArea.executeQuery'''
select distinct new map(building as name, building as value)
from UserArea where user = :user''', [user: Teacher.load(securityService.userId)]
    }

    def getPlaces() {
        Room.executeQuery'''
select distinct new map(id as id, building as building, name as name, name as value)
from Room
where building in :buildings or id = 6
order by name''', [buildings: areas]
    }

    def getAreas() {
        UserArea.executeQuery("select distinct building from UserArea where user = :user", [user: Teacher.load(securityService.userId)])
    }

    def getFormInfo(Long id) {
        def result = Asset.executeQuery'''
select new map(
a.id as id,
    a.sn as sn,
    a.code as code,
    a.name as name,
    a.state as state,
    a.dateBought as dateBought,
    a.qualifyMonth as qualifyMonth,
    a.assetType as assetType,
    a.unit as unit,
    a.pcs as pcs,
    a.note as note,
    s.name as supplier,
    r.building as building,
    r.name as place,
    m.id as assetModelId,
    m.brand as brand,
    m.specs as specs,
    m.parameter as parameter
)
from Asset a
left join a.assetModel m
left join a.supplier s
left join a.room r
where a.id = :id
''', [id: id]
        return result ? [form: result[0]] : [form: []]
    }
}
