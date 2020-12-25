package cn.edu.bnuz.bell.asset

import cn.edu.bnuz.bell.security.SecurityService
import cn.edu.bnuz.bell.security.User
import grails.gorm.transactions.Transactional

@Transactional
class AssetCenterService {
    PlaceService placeService
    SecurityService securityService

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
                areas: UserArea.executeQuery("select distinct building from UserArea where user = :user", [user: User.load(securityService.userId)]),
        ]
    }

    def getAssetNames() {
        Asset.executeQuery("select distinct new map(name as name, name as value) from Asset order by name")
    }

    def getStates() {
        Asset.executeQuery("select distinct new map(state as name, state as value) from Asset order by state")
    }

    /**
     * 粘贴板式的导入
     */
    def update(String data) {
        // 报错列表
        def error = new ArrayList<String>()
        def success = 0
        def rows = data.split("\n")
        if (rows.length) {
            rows.eachWithIndex {String row, int index ->
                def cells = row.split("\t")
                Asset asset = Asset.load(cells[0])
                if (!asset) {
                    error.add("第${index + 1}行，id${cells[0]}不存在！")
                } else {
                    success ++
                    if (asset.code) {
                        error.add("第${index + 1}行，id${cells[0]}资产编号已存在！")
                    } else {
                        asset.setCode(cells[1])
                    }
                    if (asset.sn) {
                        error.add("第${index + 1}行，id${cells[0]}序列号已存在！")
                    } else {
                        asset.setSn(cells[2])
                    }
                    if (asset.price > 0) {
                        error.add("第${index + 1}行，id${cells[0]}单价已存在！")
                    } else {
                        if (cells.length >= 4) {
                            asset.setPrice(cells[3] as BigDecimal)
                        }
                    }
                    asset.save()
                }
            }
        }
        return [error: error, success: success]
    }
}
