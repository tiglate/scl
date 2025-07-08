package ludo.mentis.aciem.scl.dev;

import java.util.HashSet;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import ludo.mentis.aciem.scl.domain.Department;
import ludo.mentis.aciem.scl.domain.Role;
import ludo.mentis.aciem.scl.domain.User;
import ludo.mentis.aciem.scl.model.Gender;
import ludo.mentis.aciem.scl.repos.DepartmentRepository;
import ludo.mentis.aciem.scl.repos.RoleRepository;
import ludo.mentis.aciem.scl.repos.UserRepository;
import ludo.mentis.aciem.scl.util.RandomUtils;
import ludo.mentis.aciem.scl.util.UserRoles;
import net.datafaker.Faker;

@Component
public class UsersLoader implements DataLoaderCommand {

	private final RandomUtils randomUtils;
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	private final DepartmentRepository departmentRepository;
	private static final String DEV_DEFAULT_PASSWORD = "12345";
	
	public UsersLoader(final RandomUtils randomUtils,
			           final UserRepository userRepository,
			           final RoleRepository roleRepository,
			           final PasswordEncoder passwordEncoder,
			           final DepartmentRepository departmentRepository) {
		this.randomUtils = randomUtils;
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
		this.departmentRepository = departmentRepository;
	}
	
	@Override
	public int getOrder() {
		return 0;
	}
	
	@Override
	public String getName() {
		return "Users";
	}
	
	@Override
	public boolean canItRun() {
		return userRepository.count() == 0;
	}
	
	@Override
	public int run() {
		return createAdmin() + createRegularUsers();
	}
	
	protected int createAdmin() {
		final var user = new User();
		final var department = departmentRepository.findByNameIgnoreCase("IT").orElseThrow();
		user.setName("admin");
        user.setEmail("admin@ampliar.dev.br");
        user.setGender(Gender.MALE);
        user.setUsername("admin");
        user.setPassword(passwordEncoder.encode(DEV_DEFAULT_PASSWORD));
        user.setEnabled(true);
        user.setDepartment(department);
        user.setRoles(new HashSet<>(roleRepository.findAll()));
        userRepository.save(user);
        return 1;
	}
	
	protected int createRegularUsers() {
		var departments = departmentRepository.findAll();
		var roles = roleRepository.findAll()
				                  .stream()
				                  .filter(p -> !UserRoles.ADMIN.equals(p.getCode()))
				                  .toList();
		var count = 0;
		for (var department : departments) {
			createFakeUser(department, roles);
			createFakeUser(department, roles);
			count += 2;
		}
		return count;
	}
	
	protected void createFakeUser(Department department, List<Role> roles) {
		final var user = new User();
		final var faker = new Faker();
		user.setName(faker.name().fullName());
        user.setEmail(faker.internet().emailAddress());
        user.setGender(randomUtils.pickRandomEnumValue(Gender.class));
        user.setUsername(faker.internet().username());
        user.setPassword(passwordEncoder.encode(faker.internet().password()));
        user.setEnabled(randomUtils.pickRandomBoolean());
        user.setDepartment(department);
        user.setRoles(new HashSet<>(randomUtils.createRandomSublist(roles, 2)));
        userRepository.save(user);
	}
}
