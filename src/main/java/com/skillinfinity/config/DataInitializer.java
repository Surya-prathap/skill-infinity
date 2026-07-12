package com.skillinfinity.config;

import com.skillinfinity.entity.Skill;
import com.skillinfinity.enums.SkillCategory;
import com.skillinfinity.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final SkillRepository skillRepository;

    @Override
    public void run(String... args) {

        addSkill("Java", SkillCategory.PROGRAMMING);
        addSkill("Python", SkillCategory.PROGRAMMING);
        addSkill("JavaScript", SkillCategory.PROGRAMMING);

        addSkill("Spring Boot", SkillCategory.FRAMEWORK);
        addSkill("React", SkillCategory.FRAMEWORK);
        addSkill("Angular", SkillCategory.FRAMEWORK);

        addSkill("MySQL", SkillCategory.DATABASE);
        addSkill("MongoDB", SkillCategory.DATABASE);

        addSkill("Docker", SkillCategory.DEVOPS);
        addSkill("Kubernetes", SkillCategory.DEVOPS);
        addSkill("AWS", SkillCategory.CLOUD);
    }

    private void addSkill(String skillName, SkillCategory category) {

        if (!skillRepository.existsBySkillName(skillName)) {

            Skill skill = Skill.builder()
                    .skillName(skillName)
                    .category(category)
                    .build();

            skillRepository.save(skill);
        }
    }
}