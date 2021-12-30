package cn.edu.bnuz.bell.asset

class PlanCommand {
    Long id

    /**
     * 计划名称
     */
    String name

    /**
     * 变动方式
     */
    String action


    /**
     * 合并相关房间
     */
    List<Long> relativePlaces

    /**
     * 状态
     */
    String status

    /**
     *新场地信息
     */
    List<RoomCommand> rooms
}
