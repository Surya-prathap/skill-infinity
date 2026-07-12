package com.skillinfinity.repository;

import com.skillinfinity.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SkillRepository extends JpaRepository<Skill, Long> {

    boolean existsBySkillName(String skillName);

}