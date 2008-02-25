/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.ofbiz.webtools.artifactinfo;

import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.eca.ServiceEcaAction;
import org.ofbiz.service.eca.ServiceEcaCondition;
import org.ofbiz.service.eca.ServiceEcaRule;
import org.ofbiz.webtools.artifactinfo.ArtifactInfoFactory.ArtifactInfoContext;

/**
 *
 */
public class ServiceEcaArtifactInfo {
    protected ArtifactInfoContext aic;
    protected ServiceEcaRule serviceEcaRule;
    protected String displayPrefix = null;
    
    public ServiceEcaArtifactInfo(ServiceEcaRule serviceEcaRule, ArtifactInfoContext aic) throws GenericServiceException {
        this.aic = aic;
        this.serviceEcaRule = serviceEcaRule;
    }
    
    public ServiceEcaRule getServiceEcaRule() {
        return this.serviceEcaRule;
    }
    
    public void setDisplayPrefix(String displayPrefix) {
        this.displayPrefix = displayPrefix;
    }
    
    public String getDisplayPrefixedName() {
        return (this.displayPrefix != null ? this.displayPrefix : "") + this.serviceEcaRule.getShortDisplayName();
    }
    
    public List<ServiceArtifactInfo> getServicesCalledByServiceEcaActions() {
        List<ServiceArtifactInfo> serviceList = FastList.newInstance();
        // TODO: *implement this
        return serviceList;
    }
    
    public List<ServiceArtifactInfo> getServicesTriggeringServiceEca() {
        List<ServiceArtifactInfo> serviceList = FastList.newInstance();
        // TODO: *implement this
        return serviceList;
    }

    public Map<String, Object> createEoModelMap(List<ServiceArtifactInfo> relatedServiceList, boolean useMoreDetailedNames) {
        if (relatedServiceList == null) relatedServiceList = FastList.newInstance();
        Map<String, Object> topLevelMap = FastMap.newInstance();

        topLevelMap.put("name", this.getDisplayPrefixedName());
        topLevelMap.put("className", "EOGenericRecord");

        // for classProperties add attribute names AND relationship names to get a nice, complete chart
        List<String> classPropertiesList = FastList.newInstance();
        topLevelMap.put("classProperties", classPropertiesList);
        // actions
        for (ServiceEcaAction ecaAction: this.serviceEcaRule.getEcaActionList()) {
            if (useMoreDetailedNames) {
                classPropertiesList.add(ecaAction.getShortDisplayDescription());
            } else {
                classPropertiesList.add(ecaAction.getServiceName());
            }
        }
        // conditions
        for (ServiceEcaCondition ecaCondition: this.serviceEcaRule.getEcaConditionList()) {
            classPropertiesList.add(ecaCondition.getShortDisplayDescription(useMoreDetailedNames));
        }
        
        /* going to try this without any attributes...
        // attributes
        List<Map<String, Object>> attributesList = FastList.newInstance();
        topLevelMap.put("attributes", attributesList);
        for (ModelParam param: this.modelService.getModelParamList()) {
            Map<String, Object> attributeMap = FastMap.newInstance();
            attributesList.add(attributeMap);
            
            if (useMoreDetailedNames) {
                attributeMap.put("name", param.getShortDisplayDescription());
            } else {
                attributeMap.put("name", param.name);
            }
            attributeMap.put("valueClassName", param.type);
            attributeMap.put("externalType", param.type);
        }
        */
        
        // relationships
        List<Map<String, Object>> relationshipsMapList = FastList.newInstance();
        
        for (ServiceArtifactInfo sai: relatedServiceList) {
            Map<String, Object> relationshipMap = FastMap.newInstance();
            relationshipsMapList.add(relationshipMap);
            
            relationshipMap.put("name", sai.getDisplayPrefixedName());
            relationshipMap.put("destination", sai.getDisplayPrefixedName());
            
            // not sure if we can use these, or need them, for this type of diagram
            //relationshipMap.put("isToMany", "N");
            //relationshipMap.put("joinSemantic", "EOInnerJoin");
            //relationshipMap.put("joins", joinsMapList);
            //joinsMap.put("sourceAttribute", keyMap.getFieldName());
            //joinsMap.put("destinationAttribute", keyMap.getRelFieldName());
        }
        
        if (relationshipsMapList.size() > 0) {
            topLevelMap.put("relationships", relationshipsMapList);
        }
        
        return topLevelMap;
    }
}
