package cn.edu.bnuz.bell.asset

import grails.gorm.transactions.Transactional

@Transactional
class AssetModelService {

    def list(String q) {
        AssetModel.executeQuery'''
select new map(
    am.id as id,
    am.name as name,
    am.brand as brand,
    am.specs as specs,
    am.parameter as parameter
)
from AssetModel am
where am.name like :q or am.brand like :q or am.specs like :q
order by am.name, am.brand
''', [q: "%${q}%"]
    }

    def list() {
        AssetModel.executeQuery'''
select new map(
    am.id as id,
    am.name as name,
    am.brand as brand,
    am.specs as specs,
    am.parameter as parameter
)
from AssetModel am
order by am.name, am.brand
'''
    }

    def create(AssetModelCommand cmd) {
        AssetModel form = new AssetModel(
                name: cmd.name,
                brand: cmd.brand,
                specs: cmd.specs,
                parameter: cmd.parameter
        )
        form.save()
    }

    def getNames() {
        AssetModel.executeQuery('select distinct new map(name as name, name as value) from AssetModel order by name')
    }

    def getFormInfo(Long id) {
        def result = AssetModel.executeQuery'''
select new map(
    am.id as id,
    am.name as name,
    am.brand as brand,
    am.specs as specs,
    am.parameter as parameter
)
from AssetModel am
where am.id = :id
''', [id: id]
        return result ? result[0] : [:]
    }
}
