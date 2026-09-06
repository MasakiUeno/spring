package jp.co.sss.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import jp.co.sss.spring.entity.Company;

public interface CompanyRepository extends JpaRepository<Company, Integer> {
}