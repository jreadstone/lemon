package com.mossle.auth.web.auth;

import java.util.ArrayList;
import java.util.List;

import com.mossle.api.LocalScopeDTO;
import com.mossle.api.ScopeConnector;

import com.mossle.auth.component.RoleDefChecker;
import com.mossle.auth.domain.Role;
import com.mossle.auth.domain.RoleDef;
import com.mossle.auth.manager.RoleDefManager;
import com.mossle.auth.manager.RoleManager;
import com.mossle.auth.support.CheckRoleException;
import com.mossle.auth.support.RoleDTO;

import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.scope.ScopeHolder;
import com.mossle.core.struts2.BaseAction;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;

@Results({
        @Result(name = RoleDefAction.RELOAD, location = "role-def.do?operationMode=RETRIEVE", type = "redirect"),
        @Result(name = RoleDefAction.RELOAD_MANAGE, location = "role-def!manage.do?id=${id}&operationMode=RETRIEVE", type = "redirect") })
public class RoleDefAction extends BaseAction implements ModelDriven<RoleDef>,
        Preparable {
    public static final String RELOAD = "reload";
    public static final String RELOAD_MANAGE = "reload-manage";
    private RoleDefManager roleDefManager;
    private RoleManager roleManager;
    private MessageSourceAccessor messages;
    private Page page = new Page();
    private RoleDef model;
    private long id;
    private String name;
    private List<Long> selectedItem = new ArrayList<Long>();
    private RoleDefChecker roleDefChecker;
    private Exportor exportor = new Exportor();
    private BeanMapper beanMapper = new BeanMapper();
    private ScopeConnector scopeConnector;
    private List<RoleDTO> roleDtos = new ArrayList<RoleDTO>();

    public String execute() {
        return list();
    }

    public String list() {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromHttpRequest(ServletActionContext.getRequest());
        Long localId = scopeConnector.findLocalId(ScopeHolder.getGlobalCode(),
                ScopeHolder.getLocalCode());
        propertyFilters.add(new PropertyFilter("EQL_localId", Long
                .toString(localId)));
        page = roleDefManager.pagedQuery(page, propertyFilters);

        return SUCCESS;
    }

    public void prepareSave() {
        model = new RoleDef();
    }

    public String save() {
        try {
            // before check
            roleDefChecker.check(model);

            // after invoke
            RoleDef dest = null;

            if (id > 0) {
                dest = roleDefManager.get(id);
                beanMapper.copy(model, dest);
            } else {
                dest = model;
            }

            if (id == 0) {
                dest.setGlobalId(scopeConnector.findGlobalId(ScopeHolder
                        .getGlobalCode()));
                dest.setLocalId(scopeConnector.findLocalId(
                        ScopeHolder.getGlobalCode(), ScopeHolder.getLocalCode()));
            }

            roleDefManager.save(dest);

            addActionMessage(messages.getMessage("core.success.save", "保存成功"));
        } catch (CheckRoleException ex) {
            addActionMessage(ex.getMessage());

            return INPUT;
        }

        return RELOAD;
    }

    public String removeAll() {
        try {
            List<RoleDef> roleDefs = roleDefManager.findByIds(selectedItem);

            for (RoleDef roleDef : roleDefs) {
                roleDefChecker.check(roleDef);
            }

            roleDefManager.removeAll(roleDefs);
            addActionMessage(messages.getMessage("core.success.delete", "删除成功"));
        } catch (CheckRoleException ex) {
            addActionMessage(ex.getMessage());
        }

        return RELOAD;
    }

    public String input() {
        if (id > 0) {
            model = roleDefManager.get(id);
        }

        return INPUT;
    }

    public void exportExcel() throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromHttpRequest(ServletActionContext.getRequest());
        page = roleDefManager.pagedQuery(page, propertyFilters);

        List<RoleDef> roleDefs = (List<RoleDef>) page.getResult();
        TableModel tableModel = new TableModel();
        tableModel.setName("role");
        tableModel.addHeaders("id", "name", "descn");
        tableModel.setData(roleDefs);
        exportor.exportExcel(ServletActionContext.getResponse(), tableModel);
    }

    public void checkName() throws Exception {
        Long localId = scopeConnector.findLocalId(ScopeHolder.getGlobalCode(),
                ScopeHolder.getLocalCode());
        String hql = "from RoleDef where localId=" + localId + " and name=?";
        Object[] params = { name };

        if (id != 0L) {
            hql = "from RoleDef where localId=" + localId
                    + " and name=? and id<>?";
            params = new Object[] { name, id };
        }

        boolean result = roleDefManager.findUnique(hql, params) == null;
        ServletActionContext.getResponse().getWriter()
                .write(Boolean.toString(result));
    }

    public String manage() throws Exception {
        RoleDef roleDef = roleDefManager.get(id);
        List<Role> roles = roleManager.findBy("roleDef.id", id);

        Long localScopeId = scopeConnector.findLocalId(
                ScopeHolder.getGlobalCode(), ScopeHolder.getLocalCode());
        LocalScopeDTO currentLocalScope = scopeConnector
                .getLocalScope(localScopeId);
        List<LocalScopeDTO> localScopeDtos;

        if (currentLocalScope.isShared()) {
            localScopeDtos = scopeConnector.findLocalScopes();
        } else {
            localScopeDtos = new ArrayList<LocalScopeDTO>();
            localScopeDtos.add(currentLocalScope);
        }

        for (LocalScopeDTO localScopeDto : localScopeDtos) {
            Role existedRole = null;

            for (Role role : roles) {
                if (role.getLocalId().equals(localScopeDto.getId())) {
                    existedRole = role;

                    break;
                }
            }

            if (existedRole == null) {
                RoleDTO roleDto = new RoleDTO();
                roleDto.setName(roleDef.getName());
                roleDto.setLocalId(localScopeDto.getId());
                roleDto.setStatus("added");
                roleDtos.add(roleDto);
            } else {
                RoleDTO roleDto = new RoleDTO();
                roleDto.setName(roleDef.getName());
                roleDto.setId(existedRole.getId());
                roleDto.setLocalId(localScopeDto.getId());
                roleDto.setStatus("existed");
                roleDtos.add(roleDto);
            }
        }

        for (Role role : roles) {
            boolean existed = false;

            for (LocalScopeDTO localScopeDto : localScopeDtos) {
                if (role.getLocalId().equals(localScopeDto.getId())) {
                    existed = true;

                    break;
                }
            }

            if (!existed) {
                RoleDTO roleDto = new RoleDTO();
                roleDto.setName(roleDef.getName());
                roleDto.setId(role.getId());
                roleDto.setLocalId(role.getLocalId());
                roleDto.setStatus("removed");
                roleDtos.add(roleDto);
            }
        }

        return "manage";
    }

    public String sync() throws Exception {
        RoleDef roleDef = roleDefManager.get(id);
        List<Role> roles = roleManager.findBy("roleDef.id", id);

        Long localScopeId = scopeConnector.findLocalId(
                ScopeHolder.getGlobalCode(), ScopeHolder.getLocalCode());
        LocalScopeDTO currentLocalScope = scopeConnector
                .getLocalScope(localScopeId);
        List<LocalScopeDTO> localScopeDtos;

        if (currentLocalScope.isShared()) {
            localScopeDtos = scopeConnector.findLocalScopes();
        } else {
            localScopeDtos = new ArrayList<LocalScopeDTO>();
            localScopeDtos.add(currentLocalScope);
        }

        for (LocalScopeDTO localScopeDto : localScopeDtos) {
            Role existedRole = null;

            for (Role role : roles) {
                if (role.getLocalId().equals(localScopeDto.getId())) {
                    existedRole = role;

                    break;
                }
            }

            if (existedRole == null) {
                Role role = new Role();
                role.setName(roleDef.getName());
                role.setRoleDef(roleDef);
                role.setGlobalId(localScopeDto.getGlobalId());
                role.setLocalId(localScopeDto.getId());
                roleManager.save(role);

                // RoleDTO roleDto = new RoleDTO();
                // roleDto.setName(roleDef.getName());
                // roleDto.setLocalId(localScopeDto.getId());
                // roleDto.setStatus("added");
                // roleDtos.add(roleDto);
            } else {
                // RoleDTO roleDto = new RoleDTO();
                // roleDto.setName(roleDef.getName());
                // roleDto.setId(existedRole.getId());
                // roleDto.setLocalId(localScopeDto.getId());
                // roleDto.setStatus("existed");
                // roleDtos.add(roleDto);
            }
        }

        for (Role role : roles) {
            boolean existed = false;

            for (LocalScopeDTO localScopeDto : localScopeDtos) {
                if (role.getLocalId().equals(localScopeDto.getId())) {
                    existed = true;

                    break;
                }
            }

            if (!existed) {
                roleManager.remove(role);

                // RoleDTO roleDto = new RoleDTO();
                // roleDto.setName(roleDef.getName());
                // roleDto.setId(role.getId());
                // roleDto.setLocalId(role.getLocalId());
                // roleDto.setStatus("removed");
                // roleDtos.add(roleDto);
            }
        }

        return RELOAD_MANAGE;
    }

    // ~ ======================================================================
    public void prepare() {
    }

    public RoleDef getModel() {
        return model;
    }

    public void setRoleDefManager(RoleDefManager roleDefManager) {
        this.roleDefManager = roleDefManager;
    }

    public void setRoleManager(RoleManager roleManager) {
        this.roleManager = roleManager;
    }

    public void setRoleDefChecker(RoleDefChecker roleDefChecker) {
        this.roleDefChecker = roleDefChecker;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
    }

    // ~ ======================================================================
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Page getPage() {
        return page;
    }

    public void setSelectedItem(List<Long> selectedItem) {
        this.selectedItem = selectedItem;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setScopeConnector(ScopeConnector scopeConnector) {
        this.scopeConnector = scopeConnector;
    }

    public List<RoleDTO> getRoleDtos() {
        return roleDtos;
    }
}
