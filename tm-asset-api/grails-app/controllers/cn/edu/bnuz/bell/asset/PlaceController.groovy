package cn.edu.bnuz.bell.asset

import cn.edu.bnuz.bell.security.SecurityService
import org.springframework.security.access.prepost.PreAuthorize


@PreAuthorize('hasAuthority("PERM_ASSET_VIEW")')
class PlaceController {
    AreaService areaService
    PlaceService placeService
    LabelService labelService
    SecurityService securityService

    def index(RoomOptionCommand cmd) {
        def createAble = securityService.hasPermission('PERM_ASSET_PLACE_WRITE')
        if (!createAble) {
            cmd.rooms = areaService.areas
        }
        renderJson([createAble: createAble, rooms: placeService.list(cmd)])
    }

    def create(){
        renderJson placeService.formForCreate
    }

    @PreAuthorize('hasAuthority("PERM_ASSET_PLACE_WRITE")')
    def save() {
        RoomCommand cmd = new RoomCommand()
        bindData(cmd, request.JSON)
        def form = placeService.create(cmd)
        renderJson([id: form.id])
    }

    def show(Long id) {
        def labels = labelService.labels()
        def labelTypes = labelService.labelTypes()
        if (!securityService.hasRole('ROLE_ASSET_LABEL_ADMIN')) {
            labels = labels.grep {
                it.single == true
            }
            labelTypes = labelTypes.grep {
                it.single == true
            }
        }
        renderJson([createAble:  securityService.hasPermission('PERM_ASSET_PLACE_WRITE'),
                    form: placeService.getFormInfo(id),
                    labels: labels,
                    labelTypes: labelTypes
        ])
    }

    @PreAuthorize('hasAuthority("PERM_ASSET_PLACE_WRITE")')
    def edit(Long id) {
        renderJson placeService.getFormForEdit(id)
    }


    def update(Long id) {
        RoomCommand cmd = new RoomCommand()
        bindData(cmd, request.JSON)
        cmd.id = id
        placeService.update(cmd)
        renderOk()
    }

    @PreAuthorize('hasAuthority("PERM_ASSET_PLACE_WRITE")')
    /**
     * 删除
     */
    def delete(Long id) {
        if (id > 100) {
            placeService.delete(id)
            renderOk()
        } else {
            renderForbidden()
        }
    }
}
