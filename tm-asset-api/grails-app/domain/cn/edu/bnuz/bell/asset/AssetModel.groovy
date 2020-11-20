package cn.edu.bnuz.bell.asset

class AssetModel {

    /**
     * 类别
     */
    String name

    /**
     * 品牌
     */
    String brand

    /**
     * 规格
     */
    String specs

    /**
     * 参数
     */
    String parameter

    static mapping = {
        comment '规格型号'
        table schema: 'tm_asset'
        id generator: 'identity', comment: 'ID'
        name type: 'text', comment: '类别'
        brand type: 'text', comment: '品牌'
        specs type: 'text', comment: '型号'
        parameter type: 'text', comment: '参数'
    }
    static constraints = {
        parameter nullable: true
        parameter(unique: ['name', 'brand', 'specs'])
    }
}
