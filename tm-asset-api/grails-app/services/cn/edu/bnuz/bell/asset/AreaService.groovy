package cn.edu.bnuz.bell.asset

import cn.edu.bnuz.bell.organization.Teacher
import cn.edu.bnuz.bell.security.SecurityService
import grails.converters.JSON
import grails.gorm.transactions.Transactional

@Transactional
class AreaService {
    SecurityService securityService
    AssetModelService assetModelService

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
and not (r.id between 2 and 5)
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
where (building in :buildings
and not (id between 2 and 5)
and status <> 'DELETED')
or id = 1
order by name
''', [buildings: areas]
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
    a.price as price,
    s.name as supplier,
    s.id as supplierId,
    r.building as building,
    r.name as place,
    t.level2 as placeType,
    m.id as assetModelId,
    m.brand as brand,
    m.specs as specs,
    m.parameter as parameter
)
from Asset a
left join a.assetModel m
left join a.supplier s
left join a.room r
left join r.placeType t
where a.id = :id
''', [id: id]
        if (result) {
            if (!securityService.hasRole('ROLE_ASSET_CENTER_ADMIN')) {
                result[0]['price'] = null
            }
            return [form: result[0], changeLogs: getChangeLogs(id)]
        } else {
            return [form: []]
        }
    }

    def getChangeLogs(Long assetId) {
        def logs = AssetChangeLog.findAllByAsset(Asset.load(assetId))
        def result = []
        logs.each { item ->
            def from = JSON.parse(item.fromValue)
            def to = JSON.parse(item.toValue)
            result += [
                    modelFrom: from.assetModelId ? assetModelService.getFormInfo(from.assetModelId) : [:],
                    otherFrom: from,
                    supplierFrom: from.supplierId ? Supplier.load(from.supplierId)?.name : null,
                    modelTo: to.assetModelId ? assetModelService.getFormInfo(to.assetModelId) : [:],
                    otherTo: to,
                    supplierTo: to.supplierId ? Supplier.load(to.supplierId)?.name : null,
                    dateCreated: item.dateCreated
            ]
        }
        return result
    }
}
