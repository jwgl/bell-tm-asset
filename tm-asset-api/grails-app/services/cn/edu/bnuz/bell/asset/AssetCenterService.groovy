package cn.edu.bnuz.bell.asset

import cn.edu.bnuz.bell.http.BadRequestException
import cn.edu.bnuz.bell.security.SecurityService
import cn.edu.bnuz.bell.security.User
import grails.converters.JSON
import grails.gorm.transactions.Transactional

import java.time.LocalDate

@Transactional
class AssetCenterService {
    PlaceService placeService
    SecurityService securityService
    LogService logService

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
    t.level2 as placeType,
    m.id as assetModelId,
    m.brand as brand,
    m.specs as specs,
    m.parameter as parameter
)
from Asset a
left join a.assetModel m
left join a.room r
left join r.placeType t
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
                areas: UserArea.executeQuery(
                        "select distinct r.building from UserArea u join u.room r"),
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
    def batchUpdate(String data) {
        // 报错列表
        def error = new ArrayList<String>()
        def success = 0
        def rows = data.split("\n")
        if (rows.length) {
            Map ths = [:]
            rows.eachWithIndex {String row, int index ->
                def cells = row.split("\t")
                if (index == 0) {
                    ths = findIndex(cells)
                } else if (ths.id != -1) {
                    Asset asset = Asset.load(cells[ths.id] as Long)
                    if (!asset) {
                        error.add("第${index + 1}行，id${cells[ths.id]}不存在！")
                    } else {
                        success++
                        if (ths.code != -1) {
                            if (asset.code) {
                                error.add("第${index + 1}行，id${cells[ths.id]}资产编号已存在！")
                            } else {
                                asset.setCode(cells[ths.code])
                            }
                        }
                        if (ths.sn != -1) {
                            if (asset.sn) {
                                error.add("第${index + 1}行，id${cells[ths.id]}序列号已存在！")
                            } else {
                                asset.setSn(cells[ths.sn])
                            }
                        }
                        if (ths.price != -1) {
                            if (asset.price > 0) {
                                error.add("第${index + 1}行，id${cells[ths.id]}单价已存在！")
                            } else {
                                asset.setPrice(cells[ths.price] as BigDecimal)
                            }
                        }
                    }
                    asset.save()
                }
            }
        }
        return [error: error, success: success]
    }

    def update(AssetCommand cmd) {
        Asset asset = Asset.load(cmd.id)
        if (asset) {
            AssetChangeLog assetChangeLog = new AssetChangeLog(
                    asset: asset,
                    fromValue: ([
                            assetModelId: asset.assetModelId,
                            supplierId: asset.supplierId,
                            price: asset.price,
                            dateBought: asset.dateBought,
                            note: asset.note
                            ] as JSON).toString(),
                    toValue: "${cmd as JSON}",
                    sake: cmd.sake,
                    dateCreated: new Date()
            )
            if (!assetChangeLog.save()) {
                assetChangeLog.errors.each {
                    println it
                }
            }
            asset.setAssetModel(AssetModel.load(cmd.assetModelId))
            asset.setSupplier(cmd.supplierId ? Supplier.load(cmd.supplierId) : null)
            asset.setPrice(cmd.price)
            asset.setDateBought(cmd.dateBought ? LocalDate.parse(cmd.dateBought) : null)
            asset.setNote(cmd.note)
            asset.save(flush: true)
            logService.log('UPDATE', '设备信息变更', asset.room, asset)
        }
    }

    Map findIndex(def headers) {
        def colIndex = [id: -1, code: -1, sn: -1, price: -1]
        headers.eachWithIndex{ name, int index ->
            switch (name?.toLowerCase()) {
                case 'id': colIndex.id = index
                    break
                case '资产编号': colIndex.code = index
                    break
                case '设备序列号': colIndex.sn = index
                    break
                case '单价': colIndex.price = index
                    break
                default:
                    throw new BadRequestException('存在不认识的列，请按要求标注列名！')
            }
        }
        return colIndex
    }
}
