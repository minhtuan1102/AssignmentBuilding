package com.javaweb.repository.custom.impl;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import com.javaweb.builder.BuildingSearchBuilder;
import com.javaweb.repository.BuildingRepository;
import com.javaweb.repository.custom.BuildingRepositoryCustom;
import com.javaweb.repository.entity.BuildingEntity;
import com.javaweb.utils.ConnectJDBCUtil;
import com.javaweb.utils.NumberUtil;
import com.javaweb.utils.StringUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Repository
public class BuildingRepositoryImpl implements BuildingRepositoryCustom  {
	
	@PersistenceContext
	private EntityManager entityManager;
	
	public static void joinTable(BuildingSearchBuilder buildingSearchBuilder, StringBuilder sql) {
		Long staffId = buildingSearchBuilder.getStaffId();
		if(staffId != null) {
			sql.append(" INNER JOIN assignmentbuilding ON b.id = assignmentbuilding.buildingid ");
		}
		List<String> typeCode = buildingSearchBuilder.getTypeCode();
		if(typeCode!=null && typeCode.size() != 0) {
			sql.append(" INNER JOIN buildingrenttype ON b.id = buildingrenttype.buildingid");
			sql.append(" INNER JOIN renttype ON renttype.id = buildingrenttype.renttypeid");
		}
	}
	public static void queryNormal(BuildingSearchBuilder buildingSearchBuilder, StringBuilder where) {
//	    for(Map.Entry<String,Object> it : params.entrySet()) {
//	        if(!it.getKey().equals("staffId") && !it.getKey().equals("typeCode") 
//	                && !it.getKey().startsWith("area") && !it.getKey().startsWith("rentPrice")) {
//	            String value = it.getValue().toString();
//	            if(StringUtil.checkString(value)) {
//	                if(NumberUtil.isNumber(value)) {
//	                    where.append(" AND b." + it.getKey() +" = "+ value);
//	                } else {
//	                    if(it.getKey().equals("name")) {
//	                        where.append(" AND b." + it.getKey() +" LIKE '%"+ value + "%'");
//	                    } else {
//	                        where.append(" AND b." + it.getKey() +" = '"+ value + "'");
//	                    }
//	                }
//	            }
//	        }
//	    }
		try {
			Field[] fields = BuildingSearchBuilder.class.getDeclaredFields();
			for(Field item : fields) {
				item.setAccessible(true);
				String fieldName = item.getName();
				if(!fieldName.equals("staffId") && !fieldName.equals("typeCode") 
	                && !fieldName.startsWith("area") && !fieldName.startsWith("rentPrice")){
					 Object value = item.get(buildingSearchBuilder);
					 if(value != null) {
						 if(item.getType().getName().equals("java.lang.Long")) {
			                    where.append(" AND b." + fieldName +" = "+ value);
			                } else {
			                	where.append(" AND b." + fieldName +" LIKE '%"+ value + "%'");
			                }
			         
			                
					 }
				}
						
			}
			
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	public static void querySpecial(BuildingSearchBuilder buildingSearchBuilder, StringBuilder where) {
		Long staffId = buildingSearchBuilder.getStaffId();
		if(staffId != null) {
	        where.append(" AND assignmentbuilding.staffid = " + staffId);
	    }
	    
	    Long rentAreaTo = buildingSearchBuilder.getAreaTo();
	    Long rentAreaFrom = buildingSearchBuilder.getAreaFrom();
	    if(rentAreaFrom != null || rentAreaTo != null) {
	    	where.append(" AND EXISTS (SELECT * FROM rentarea r WHERE b.id = r.buildingid");
	        if(rentAreaFrom != null) {
	            where.append(" AND r.value >= " + rentAreaFrom);
	        }
	        if(rentAreaTo != null) {
	            where.append(" AND r.value <= " + rentAreaTo);
	        }
	        where.append(") ");
	    }
	    
	    Long rentPriceTo = buildingSearchBuilder.getRentPriceTo();
	    Long rentPriceFrom = buildingSearchBuilder.getRentPriceFrom();
	    if(rentPriceFrom != null || rentPriceTo!= null) {
	        if(rentPriceFrom != null) {
	            where.append(" AND b.rentprice >= " + rentPriceFrom);
	        }
	        if(rentPriceTo!= null) {
	            where.append(" AND b.rentprice <= " + rentPriceTo);
	        }
	    }
	    //java 7
//	    if(typeCode != null && typeCode.size() > 0) {
//	        List<String> code = new ArrayList<>();
//	        for(String item : typeCode) {
//	            code.add("'" + item + "'");
//	        }
//	        where.append(" AND renttype.code IN(" + String.join(",", code) + ")");
//	    }
	    //java 8
	    List<String> typeCode = buildingSearchBuilder.getTypeCode();
	    if(typeCode != null && typeCode.size() > 0) {
	    	where.append(" AND(");
	    	String sql = typeCode.stream().map(it->"renttype.code Like" + "'%" + it + "%'").collect(Collectors.joining(" OR "));
	    	where.append(sql);
	    	where.append(" ) ");
	    }
	    
	}
//	@Override
	public List<BuildingEntity> findAll(BuildingSearchBuilder buildingSearchBuilder) {
		StringBuilder sql = new StringBuilder("SELECT b.* FROM building b ");
		joinTable(buildingSearchBuilder, sql);
		StringBuilder where = new StringBuilder(" WHERE 1=1");
		queryNormal(buildingSearchBuilder,where);
		querySpecial(buildingSearchBuilder, where);
		where.append(" GROUP BY b.id;");
		sql.append(where);	
		Query query = entityManager.createNativeQuery(sql.toString(), BuildingEntity.class);
		return query.getResultList();
		// TODO Auto-generated method stub
	}
		
}
