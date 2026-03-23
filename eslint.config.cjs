module.exports = [
    {
        files: ["**/*.js", "**/*.mjs", "**/*.cjs"],
        languageOptions: {
            ecmaVersion: "latest",
            sourceType: "module",
        },
        ignores: ["node_modules/**", "target/**"],
        rules: {
            "no-unused-vars": ["warn", { "argsIgnorePattern": "^_" }],
            "no-console": "off",
            "eqeqeq": ["error", "smart"],
            "curly": ["error", "multi-line"],
            "semi": ["error", "always"],
            "indent": ["error", 4]
        }
    }
];
