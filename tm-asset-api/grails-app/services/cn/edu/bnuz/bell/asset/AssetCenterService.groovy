package cn.edu.bnuz.bell.asset

import grails.gorm.transactions.Transactional

@Transactional
class AssetCenterService {
    PlaceService placeService

    def list(AssetOptionCommand cmd) {
        def sqlStr = '''
select new map(
    a.id as id,
    a.sn as sn,
    a.code as code,
    a.name as name,
    a.price as price,
    a.unit as unit,
    a.dateBought as dateBought,
    a.qualifyMonth as qualifyMonth,
    a.assetType as assetType,
    a.pcs as pcs,
    a.note as note,
    a.state as state,
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
left join a.room r
left join a.supplier s
'''
        if (!cmd.criterion.isEmpty()) {
            sqlStr += " where ${cmd.criterion} order by r.building, r.name"
        }
        def list = Asset.executeQuery sqlStr, cmd.args
        return [
                list: list,
                buildings: placeService.buildings,
                places: placeService.places,
                assetNames: assetNames,
                states: states,
        ]
    }

    def getAssetNames() {
        Asset.executeQuery("select distinct new map(name as name, name as value) from Asset order by name")
    }

    def getStates() {
        Asset.executeQuery("select distinct new map(state as name, state as value) from Asset order by state")
    }
}
