package com.javaweb.api;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.stereotype.Controller;
import com.javaweb.model.BuildingDTO;
import com.javaweb.model.BuildingResquestDTO;
import com.javaweb.model.ErrorRespondDTO;
import com.javaweb.repository.BuildingRepository;
import com.javaweb.repository.entity.BuildingEntity;
import com.javaweb.repository.entity.DistrictEntity;
import com.javaweb.service.BuildingService;

import customexception.FieldRequiredException;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@RestController
@Transactional
public class BuildingAPI {
	
	@Autowired			
	private BuildingService buildingService;
	
	@Autowired
	private BuildingRepository buildingRepository;
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@GetMapping(value = "/api/building/")
	public List<BuildingDTO> GetBuilding(@RequestParam Map<String,Object> params,
										@RequestParam(name="typeCode", required = false) List<String>typeCode) {
		List<BuildingDTO> result = buildingService.findAll(params,typeCode);
		return result;	
	}
	
	@GetMapping(value = "/api/building/{id}")
	public BuildingDTO GetBuildingById(@PathVariable Long id) {
		BuildingDTO result = new BuildingDTO();
		BuildingEntity building = buildingRepository.findById(id).get(); 
		return result;	
	}
	
	@PostMapping(value = "/api/building/")
	public void createBuilding(@RequestBody BuildingResquestDTO buildingResquestDTO) {
		BuildingEntity builEntity = new BuildingEntity();	
		builEntity.setName(buildingResquestDTO.getName()); 
		builEntity.setStreet(buildingResquestDTO.getStreet());
		builEntity.setWard(buildingResquestDTO.getWard());
		DistrictEntity districtEntity = new DistrictEntity();
		districtEntity.setId(buildingResquestDTO.getDistrictID());
		builEntity.setDistrict(districtEntity);
		entityManager.persist(builEntity);
		System.out.println("ok");
}
	@PutMapping(value = "/api/building/")
	public void updateBuilding(@RequestBody BuildingResquestDTO buildingResquestDTO) {
		BuildingEntity builEntity = new BuildingEntity();	
		builEntity.setId(1L);
		builEntity.setName(buildingResquestDTO.getName()); 
		builEntity.setStreet(buildingResquestDTO.getStreet());
		builEntity.setWard(buildingResquestDTO.getWard());
		DistrictEntity districtEntity = new DistrictEntity();
		districtEntity.setId(buildingResquestDTO.getDistrictID());
		builEntity.setDistrict(districtEntity);
		entityManager.merge(builEntity);
		System.out.println("ok");
}
	@DeleteMapping(value = "api/building/{ids}")
	public void deleteBuilding(@PathVariable Long[] ids) {
		buildingRepository.deleteByIdIn(ids);
	}
}
