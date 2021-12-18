package cn.edu.bnuz.bell.asset

class PlanRoom implements Serializable {

    Plan plan
    Room room

    static belongsTo = [plan: Plan]

    static mapping = {
        comment '计划关联场地'
        table schema: 'tm_asset'
        id composite: ['plan', 'room'], comment: '计划-场地'
        plan comment: '场地变动计划'
        room comment: '场地'
    }

    boolean equals(other) {
        if (!(other instanceof PlanRoom)) {
            return false
        }

        other.plan?.id == plan?.id && other.room?.id == room?.id
    }

    int hashCode() {
        Objects.hash(plan.id, room.id)
    }
}
