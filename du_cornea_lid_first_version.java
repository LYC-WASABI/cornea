import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_cornea_lid_first_version {
  private static final String OUT = "D:\\\\COMSOL_Outputs\\\\models\\\\du\\\\";

  public static Model run() throws java.io.IOException {
    Model model = ModelUtil.create("Model");
    model.modelPath("D:\\\\COMSOL_Outputs\\\\models\\\\du");
    model.label("du_cornea_lid_first_version.mph");

    model.param().set("Rcor", "7.8[mm]", "Anterior corneal radius");
    model.param().set("tcor", "0.5[mm]", "Corneal thickness");
    model.param().set("Dcor", "12[mm]", "Corneal diameter");
    model.param().set("a_cor", "Dcor/2", "Corneal semi-diameter");
    model.param().set("z_base", "sqrt(Rcor^2-a_cor^2)", "Corneal limbus cut plane");
    model.param().set("cap_h", "Rcor-z_base", "Corneal cap height");
    model.param().set("Rlid_in", "Rcor+0.02[mm]", "Initial lid wiper inner radius");
    model.param().set("tlid", "0.8[mm]", "Lid wiper thickness");
    model.param().set("Rlid_out", "Rlid_in+tlid", "Lid wiper outer radius");
    model.param().set("lid_width", "1[mm]", "Lid wiper contact width");
    model.param().set("lid_length", "8[mm]", "Lid wiper horizontal length");
    model.param().set("theta_lid", "10[deg]", "First lid wiper position");
    model.param().set("ylid", "Rcor*sin(theta_lid)", "Lid wiper superior-inferior location");
    model.param().set("zlid", "Rcor*cos(theta_lid)", "Lid wiper corneal-surface location");
    model.param().set("Ecor", "0.4[MPa]");
    model.param().set("nucor", "0.42");
    model.param().set("rhocor", "1400[kg/m^3]");
    model.param().set("Elid", "0.42[MPa]");
    model.param().set("nulid", "0.49");
    model.param().set("rholid", "999[kg/m^3]");
    model.param().set("IOP", "15[mmHg]");
    model.param().set("k_found", "0.08[N/mm^3]", "Initial posterior elastic foundation stiffness");
    model.param().set("F_lid", "0.03[N]");
    model.param().set("p_lid", "F_lid/(lid_length*lid_width)", "Equivalent lid wiper load pressure");
    model.param().set("mu_friction", "0.1", "Dynamic friction coefficient between lid wiper and cornea");

    model.component().create("comp1", true);
    model.component("comp1").geom().create("geom1", 3);
    model.component("comp1").geom("geom1").lengthUnit("mm");

    buildCorneaGeometry(model);
    model.component("comp1").geom("geom1").run();
    model.save(OUT + "01_cornea_spherical_cap.mph");

    buildLidGeometry(model);
    model.component("comp1").geom("geom1").run();
    model.save(OUT + "02_cornea_plus_lid_wiper_geometry.mph");

    buildSelections(model);
    addMaterialsAndPhysics(model);
    model.save(OUT + "03_solid_mechanics_and_materials.mph");

    addLimbusFix(model);
    model.save(OUT + "04_limbus_fixed.mph");

    addPosteriorPressure(model);
    model.save(OUT + "05_posterior_15mmHg_pressure.mph");

    addElasticFoundation(model);
    model.save(OUT + "06_posterior_elastic_foundation.mph");

    addLidLoad(model);
    model.save(OUT + "07_lid_wiper_total_force_003N.mph");

    buildMesh(model);
    model.save(OUT + "08_meshed_first_version.mph");

    addFrictionContact(model);
    model.save(OUT + "09_friction_contact_mu_0p1.mph");

    addStationaryParametricStudy(model);
    model.save(OUT + "10_stationary_parametric_sweep_10deg_to_70deg.mph");

    refineContactMesh(model);
    model.save(OUT + "11_contact_zone_refined_mesh.mph");
    model.save(OUT + "du_cornea_lid_first_version_final.mph");
    return model;
  }

  private static void buildCorneaGeometry(Model model) {
    model.component("comp1").geom("geom1").create("sph_cor_outer", "Sphere");
    model.component("comp1").geom("geom1").feature("sph_cor_outer").set("r", "Rcor");
    model.component("comp1").geom("geom1").feature("sph_cor_outer").set("pos", new String[]{"0", "0", "0"});

    model.component("comp1").geom("geom1").create("sph_cor_inner", "Sphere");
    model.component("comp1").geom("geom1").feature("sph_cor_inner").set("r", "Rcor-tcor");
    model.component("comp1").geom("geom1").feature("sph_cor_inner").set("pos", new String[]{"0", "0", "0"});

    model.component("comp1").geom("geom1").create("dif_cor_shell", "Difference");
    model.component("comp1").geom("geom1").feature("dif_cor_shell").selection("input").set(new String[]{"sph_cor_outer"});
    model.component("comp1").geom("geom1").feature("dif_cor_shell").selection("input2").set(new String[]{"sph_cor_inner"});

    model.component("comp1").geom("geom1").create("cyl_cor_cap", "Cylinder");
    model.component("comp1").geom("geom1").feature("cyl_cor_cap").set("r", "a_cor");
    model.component("comp1").geom("geom1").feature("cyl_cor_cap").set("h", "cap_h+0.05[mm]");
    model.component("comp1").geom("geom1").feature("cyl_cor_cap").set("pos", new String[]{"0", "0", "z_base"});

    model.component("comp1").geom("geom1").create("int_cornea", "Intersection");
    model.component("comp1").geom("geom1").feature("int_cornea").selection("input").set(new String[]{"dif_cor_shell", "cyl_cor_cap"});
  }

  private static void buildLidGeometry(Model model) {
    model.component("comp1").geom("geom1").create("sph_lid_outer", "Sphere");
    model.component("comp1").geom("geom1").feature("sph_lid_outer").set("r", "Rlid_out");
    model.component("comp1").geom("geom1").feature("sph_lid_outer").set("pos", new String[]{"0", "0", "0"});

    model.component("comp1").geom("geom1").create("sph_lid_inner", "Sphere");
    model.component("comp1").geom("geom1").feature("sph_lid_inner").set("r", "Rlid_in");
    model.component("comp1").geom("geom1").feature("sph_lid_inner").set("pos", new String[]{"0", "0", "0"});

    model.component("comp1").geom("geom1").create("dif_lid_shell", "Difference");
    model.component("comp1").geom("geom1").feature("dif_lid_shell").selection("input").set(new String[]{"sph_lid_outer"});
    model.component("comp1").geom("geom1").feature("dif_lid_shell").selection("input2").set(new String[]{"sph_lid_inner"});

    model.component("comp1").geom("geom1").create("blk_lid_window", "Block");
    model.component("comp1").geom("geom1").feature("blk_lid_window").set("size", new String[]{"lid_length", "lid_width", "1.2[mm]"});
    model.component("comp1").geom("geom1").feature("blk_lid_window").set("base", "center");
    model.component("comp1").geom("geom1").feature("blk_lid_window").set("pos", new String[]{"0", "ylid", "zlid+tlid/2"});

    model.component("comp1").geom("geom1").create("int_lid", "Intersection");
    model.component("comp1").geom("geom1").feature("int_lid").selection("input").set(new String[]{"dif_lid_shell", "blk_lid_window"});

    model.component("comp1").geom("geom1").feature("fin").set("action", "assembly");
    model.component("comp1").geom("geom1").feature("fin").set("createpairs", "on");
  }

  private static void buildSelections(Model model) {
    model.component("comp1").selection().create("sel_cornea_dom", "Box");
    model.component("comp1").selection("sel_cornea_dom").set("entitydim", 3);
    model.component("comp1").selection("sel_cornea_dom").set("xmin", "-6.2[mm]");
    model.component("comp1").selection("sel_cornea_dom").set("xmax", "6.2[mm]");
    model.component("comp1").selection("sel_cornea_dom").set("ymin", "-6.2[mm]");
    model.component("comp1").selection("sel_cornea_dom").set("ymax", "6.2[mm]");
    model.component("comp1").selection("sel_cornea_dom").set("zmin", "z_base-0.1[mm]");
    model.component("comp1").selection("sel_cornea_dom").set("zmax", "Rcor+0.1[mm]");

    model.component("comp1").selection().create("sel_lid_dom", "Box");
    model.component("comp1").selection("sel_lid_dom").set("entitydim", 3);
    model.component("comp1").selection("sel_lid_dom").set("xmin", "-lid_length/2-0.1[mm]");
    model.component("comp1").selection("sel_lid_dom").set("xmax", "lid_length/2+0.1[mm]");
    model.component("comp1").selection("sel_lid_dom").set("ymin", "ylid-lid_width/2-0.1[mm]");
    model.component("comp1").selection("sel_lid_dom").set("ymax", "ylid+lid_width/2+0.1[mm]");
    model.component("comp1").selection("sel_lid_dom").set("zmin", "zlid-0.1[mm]");
    model.component("comp1").selection("sel_lid_dom").set("zmax", "zlid+tlid+0.2[mm]");

    model.component("comp1").selection().create("sel_posterior", "Ball");
    model.component("comp1").selection("sel_posterior").set("entitydim", 2);
    model.component("comp1").selection("sel_posterior").set("posx", "0");
    model.component("comp1").selection("sel_posterior").set("posy", "0");
    model.component("comp1").selection("sel_posterior").set("posz", "0");
    model.component("comp1").selection("sel_posterior").set("r", "Rcor-tcor+0.03[mm]");
    model.component("comp1").selection("sel_posterior").set("condition", "inside");

    model.component("comp1").selection().create("sel_limbus", "Box");
    model.component("comp1").selection("sel_limbus").set("entitydim", 2);
    model.component("comp1").selection("sel_limbus").set("xmin", "-6.1[mm]");
    model.component("comp1").selection("sel_limbus").set("xmax", "6.1[mm]");
    model.component("comp1").selection("sel_limbus").set("ymin", "-6.1[mm]");
    model.component("comp1").selection("sel_limbus").set("ymax", "6.1[mm]");
    model.component("comp1").selection("sel_limbus").set("zmin", "z_base-0.05[mm]");
    model.component("comp1").selection("sel_limbus").set("zmax", "z_base+0.08[mm]");

    model.component("comp1").selection().create("sel_lid_outer_load", "Box");
    model.component("comp1").selection("sel_lid_outer_load").set("entitydim", 2);
    model.component("comp1").selection("sel_lid_outer_load").set("xmin", "-lid_length/2-0.1[mm]");
    model.component("comp1").selection("sel_lid_outer_load").set("xmax", "lid_length/2+0.1[mm]");
    model.component("comp1").selection("sel_lid_outer_load").set("ymin", "ylid-lid_width/2-0.1[mm]");
    model.component("comp1").selection("sel_lid_outer_load").set("ymax", "ylid+lid_width/2+0.1[mm]");
    model.component("comp1").selection("sel_lid_outer_load").set("zmin", "zlid+tlid-0.08[mm]");
    model.component("comp1").selection("sel_lid_outer_load").set("zmax", "zlid+tlid+0.25[mm]");

    model.component("comp1").selection().create("sel_contact_zone", "Box");
    model.component("comp1").selection("sel_contact_zone").set("entitydim", 3);
    model.component("comp1").selection("sel_contact_zone").set("xmin", "-lid_length/2-0.2[mm]");
    model.component("comp1").selection("sel_contact_zone").set("xmax", "lid_length/2+0.2[mm]");
    model.component("comp1").selection("sel_contact_zone").set("ymin", "ylid-lid_width/2-0.3[mm]");
    model.component("comp1").selection("sel_contact_zone").set("ymax", "ylid+lid_width/2+0.3[mm]");
    model.component("comp1").selection("sel_contact_zone").set("zmin", "zlid-0.7[mm]");
    model.component("comp1").selection("sel_contact_zone").set("zmax", "zlid+tlid+0.3[mm]");
  }

  private static void addMaterialsAndPhysics(Model model) {
    model.component("comp1").material().create("mat_cornea", "Common");
    model.component("comp1").material("mat_cornea").label("Cornea linear elastic");
    model.component("comp1").material("mat_cornea").selection().named("sel_cornea_dom");
    model.component("comp1").material("mat_cornea").propertyGroup("def").set("youngsmodulus", "Ecor");
    model.component("comp1").material("mat_cornea").propertyGroup("def").set("poissonsratio", "nucor");
    model.component("comp1").material("mat_cornea").propertyGroup("def").set("density", "rhocor");

    model.component("comp1").material().create("mat_lid", "Common");
    model.component("comp1").material("mat_lid").label("Lid wiper linear elastic");
    model.component("comp1").material("mat_lid").selection().named("sel_lid_dom");
    model.component("comp1").material("mat_lid").propertyGroup("def").set("youngsmodulus", "Elid");
    model.component("comp1").material("mat_lid").propertyGroup("def").set("poissonsratio", "nulid");
    model.component("comp1").material("mat_lid").propertyGroup("def").set("density", "rholid");

    model.component("comp1").physics().create("solid", "SolidMechanics", "geom1");
    model.component("comp1").physics("solid").prop("ShapeProperty").set("order_displacement", 2);
    model.component("comp1").physics("solid").create("gcnt1", "GeneralContact", 2);
    model.component("comp1").physics("solid").feature("gcnt1").label("General contact: lid wiper to cornea");
  }

  private static void addLimbusFix(Model model) {
    model.component("comp1").physics("solid").create("fix_limbus", "Fixed", 2);
    model.component("comp1").physics("solid").feature("fix_limbus").selection().named("sel_limbus");
  }

  private static void addPosteriorPressure(Model model) {
    model.component("comp1").physics("solid").create("press_iop", "BoundaryLoad", 2);
    model.component("comp1").physics("solid").feature("press_iop").selection().named("sel_posterior");
    model.component("comp1").physics("solid").feature("press_iop").set("forceType", "ForceArea");
    model.component("comp1").physics("solid").feature("press_iop").set("FperArea", new String[]{"IOP*nx", "IOP*ny", "IOP*nz"});
  }

  private static void addElasticFoundation(Model model) {
    model.component("comp1").physics("solid").create("ef_posterior", "BoundaryLoad", 2);
    model.component("comp1").physics("solid").feature("ef_posterior").selection().named("sel_posterior");
    model.component("comp1").physics("solid").feature("ef_posterior").label("Posterior normal elastic foundation");
    model.component("comp1").physics("solid").feature("ef_posterior").set("forceType", "ForceArea");
    model.component("comp1").physics("solid").feature("ef_posterior").set("FperArea",
        new String[]{"-k_found*(u*nx+v*ny+w*nz)*nx",
                     "-k_found*(u*nx+v*ny+w*nz)*ny",
                     "-k_found*(u*nx+v*ny+w*nz)*nz"});
  }

  private static void addLidLoad(Model model) {
    model.component("comp1").physics("solid").create("load_lid", "BoundaryLoad", 2);
    model.component("comp1").physics("solid").feature("load_lid").selection().named("sel_lid_outer_load");
    model.component("comp1").physics("solid").feature("load_lid").set("forceType", "ForceArea");
    model.component("comp1").physics("solid").feature("load_lid").set("FperArea", new String[]{"-p_lid*nx", "-p_lid*ny", "-p_lid*nz"});
  }

  private static void buildMesh(Model model) {
    model.component("comp1").mesh().create("mesh1");
    model.component("comp1").mesh("mesh1").automatic(false);
    model.component("comp1").mesh("mesh1").create("size1", "Size");
    model.component("comp1").mesh("mesh1").feature("size1").set("hauto", 3);
    model.component("comp1").mesh("mesh1").create("ftet_custom", "FreeTet");
    model.component("comp1").mesh("mesh1").feature("ftet_custom").create("size_contact", "Size");
    model.component("comp1").mesh("mesh1").feature("ftet_custom").feature("size_contact").selection().geom("geom1", 3);
    model.component("comp1").mesh("mesh1").feature("ftet_custom").feature("size_contact").selection().named("sel_lid_dom");
    model.component("comp1").mesh("mesh1").feature("ftet_custom").feature("size_contact").set("hmax", "0.12[mm]");
    model.component("comp1").mesh("mesh1").feature("ftet_custom").feature("size_contact").set("hmin", "0.03[mm]");
    model.component("comp1").mesh("mesh1").run();
  }

  private static void addFrictionContact(Model model) {
    model.component("comp1").physics("solid").feature("dcnt1").feature().create("fric1", "Friction");
    model.component("comp1").physics("solid").feature("dcnt1").feature("fric1").label("Coulomb friction, mu = 0.1");
    model.component("comp1").physics("solid").feature("dcnt1").feature("fric1").set("mu_fric", "mu_friction");
  }

  private static void addStationaryParametricStudy(Model model) {
    model.study().create("std1");
    model.study("std1").create("stat", "Stationary");
    model.study("std1").feature("stat").set("geomselection", "geom1");
    model.study("std1").feature().create("param", "Parametric");
    model.study("std1").feature("param").set("pname", new String[]{"theta_lid"});
    model.study("std1").feature("param").set("plistarr", new String[]{"10 20 30 40 50 60 70"});
    model.study("std1").feature("param").set("punit", new String[]{"deg"});
  }

  private static void refineContactMesh(Model model) {
    model.component("comp1").mesh("mesh1").feature("ftet_custom").create("size_cornea_contact", "Size");
    model.component("comp1").mesh("mesh1").feature("ftet_custom").feature("size_cornea_contact").selection().geom("geom1", 3);
    model.component("comp1").mesh("mesh1").feature("ftet_custom").feature("size_cornea_contact").selection().named("sel_contact_zone");
    model.component("comp1").mesh("mesh1").feature("ftet_custom").feature("size_cornea_contact").set("hmax", "0.08[mm]");
    model.component("comp1").mesh("mesh1").feature("ftet_custom").feature("size_cornea_contact").set("hmin", "0.02[mm]");
    model.component("comp1").mesh("mesh1").run();
  }

  public static void main(String[] args) throws java.io.IOException {
    run();
  }
}
