package com.javaweb.repository.impl;

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

import org.springframework.stereotype.Repository;

import com.javaweb.builder.BuildingSearchBuilder;
import com.javaweb.repository.BuildingRepository;
import com.javaweb.repository.entity.BuildingEntity;
import com.javaweb.utils.ConnectJDBCUtil;
import com.javaweb.utils.NumberUtil;
import com.javaweb.utils.StringUtil;

@Repository
public class BuildingRepositoryImpl implements  BuildingRepository {
	
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
			                }
						 else {
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
	    	where.append(" AND EXISTS (SELECT * FROM rentarea r WHERE b.id = r.buildingid ");
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
	    	String sql = typeCode.stream().map(it->"renttype.code Like " + "'%" + it + "%'").collect(Collectors.joining(" OR "));
	    	where.append(sql);
	    	where.append(" ) ");
	    }
	    
	}
	@Override
	public List<BuildingEntity> findAll(BuildingSearchBuilder buildingSearchBuilder) {
		StringBuilder sql = new StringBuilder("SELECT DISTINCT b.id, b.name, b.districtid, b.street, b.ward, b.numberofbasement, b.floorarea, b.rentprice, " + " b.managername, b.managerphonenumber, b.servicefee,b.brokeragefee" + "\nFROM building b ");
		joinTable(buildingSearchBuilder, sql);
		StringBuilder where = new StringBuilder(" WHERE 1=1 ");
		queryNormal(buildingSearchBuilder,where);
		querySpecial(buildingSearchBuilder,where);
		where.append(" GROUP BY b.id ");
		sql.append(where);	
		System.out.print(sql);
		List<BuildingEntity> result = new ArrayList<>();
		try(Connection conn = ConnectJDBCUtil.getConnetion();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql.toString());
				){
			while(rs.next()) {
				BuildingEntity buildingEntity = new BuildingEntity();
				buildingEntity.setId(rs.getLong("b.id"));
				buildingEntity.setName(rs.getString("b.name"));
				buildingEntity.setWard(rs.getString("b.ward"));
				buildingEntity.setStreet(rs.getString("b.street"));
				buildingEntity.setDistrictId(rs.getLong("b.districtid"));
				buildingEntity.setManagerName(rs.getString("b.managername"));
				buildingEntity.setManagerPhoneNumber(rs.getString("b.managerphonenumber"));
				buildingEntity.setFloorArea(rs.getLong("b.floorarea"));
				buildingEntity.setRentPrice(rs.getLong("b.rentprice"));
				buildingEntity.setServiceFee(rs.getString("b.servicefee"));
				buildingEntity.setBrokerageFee(rs.getString("b.brokeragefee"));
				result.add(buildingEntity);
			}
			
		}catch(SQLException e) {
			e.printStackTrace();
			System.out.println("Connected database failed...");
			}
		return result;
		// TODO Auto-generated method stub
	}
		
}
