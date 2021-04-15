package cn.edu.bnuz.bell.asset

import cn.edu.bnuz.bell.organization.Teacher
import grails.gorm.transactions.Transactional

import java.time.LocalDate

@Transactional
class AssetCartService {

    def list(String userId) {
        AssetCart.executeQuery'''
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
    r.building as building,
    r.name as place,
    m.id as assetModelId,
    m.brand as brand,
    m.specs as specs,
    m.parameter as parameter
)
from AssetCart c
join c.asset a
join c.user u
left join a.assetModel m
left join a.room r
where c.name is null and u.id = :userId
order by c.id desc
''', [userId: userId]
    }

    def history(String userId) {
        AssetCart.executeQuery'''
select distinct new map(
    a.name as name,
    a.dateCreated as dateCreated,
    u.name as userName,
    count(*) as counts
)
from AssetCart a
join a.user u
where a.name is not null and u.id = :userId
group by a.name,a.dateCreated,u.name
''', [userId: userId]
    }

    def getCartItems(String userId, String cartName) {
        AssetCart.executeQuery'''
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
    r.building as building,
    r.name as place,
    m.id as assetModelId,
    m.brand as brand,
    m.specs as specs,
    m.parameter as parameter
)
from AssetCart c
join c.asset a
join c.user u
left join a.assetModel m
left join a.room r
where c.name = :cartName and u.id = :userId
order by c.id desc
''', [userId: userId, cartName: cartName]
    }

    def create(String userId, CartCommand cmd) {
        AssetCart.executeUpdate'''
update AssetCart
set name = :cartName, dateCreated = :date
where user.id = :userId and asset.id in (:ids)''', [userId: userId, cartName: cmd.cartName, date: LocalDate.now(), ids: cmd.ids]
    }

    def delete(String userId) {
        AssetCart.executeUpdate('delete from AssetCart where user.id = :userId and name is null', [userId: userId])
    }
}
